//
//  SavedPaymentMethodCollectionView.swift
//  Stripe
//
//  Created by Yuki Tokuhiro on 9/3/20.
//  Copyright © 2020 Stripe, Inc. All rights reserved.
//

import Foundation
import UIKit

// MARK: - Constants
/// Entire cell size
private let cellSize: CGSize = CGSize(width: 100, height: 88)
/// Size of the rounded rectangle that contains the PM logo
let roundedRectangleSize = CGSize(width: 100, height: 64)
private let paymentMethodLogoSize: CGSize = CGSize(width: 54, height: 40)

// MARK: - SavedPaymentMethodCollectionView
class SavedPaymentMethodCollectionView: UICollectionView {
    init() {
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal
        layout.sectionInset = UIEdgeInsets(
            top: 0, left: PaymentSheetUI.defaultPadding, bottom: 0,
            right: PaymentSheetUI.defaultPadding)
        layout.itemSize = cellSize
        layout.minimumInteritemSpacing = 12
        super.init(frame: .zero, collectionViewLayout: layout)

        showsHorizontalScrollIndicator = false
        backgroundColor = CompatibleColor.systemBackground

        register(
            PaymentOptionCell.self, forCellWithReuseIdentifier: PaymentOptionCell.reuseIdentifier)
    }

    var isRemovingPaymentMethods: Bool = false

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override var intrinsicContentSize: CGSize {
        return CGSize(width: UIView.noIntrinsicMetric, height: 100)
    }
}

// MARK: - Cells

protocol PaymentOptionCellDelegate: AnyObject {
    func paymentOptionCellDidSelectRemove(
        _ paymentOptionCell: SavedPaymentMethodCollectionView.PaymentOptionCell)
}

extension SavedPaymentMethodCollectionView {

    /// A rounded, shadowed cell with an icon (e.g. Apple Pay, VISA, ➕) and some text at the bottom.
    /// Has a green outline when selected
    class PaymentOptionCell: UICollectionViewCell, EventHandler {
        static let reuseIdentifier = "PaymentOptionCell"

        lazy var label: UILabel = {
            let label = UILabel()
            label.font = UIFont.preferredFont(forTextStyle: .footnote, weight: .medium)
            label.textColor = CompatibleColor.label
            return label
        }()
        let paymentMethodLogo: UIImageView = UIImageView()
        let plus: CircleIconView = CircleIconView(icon: .plus)
        let selectedIcon: CircleIconView = CircleIconView(icon: .checkmark)
        lazy var shadowRoundedRectangle: ShadowedRoundedRectangle = {
            let shadowRoundedRectangle = ShadowedRoundedRectangle()
            shadowRoundedRectangle.layoutMargins = UIEdgeInsets(
                top: 15, left: 24, bottom: 15, right: 24)
            return shadowRoundedRectangle
        }()
        let deleteButton = CircularButton(style: .remove)

        fileprivate var viewModel: SavedPaymentOptionsViewController.Selection? = nil

        var isRemovingPaymentMethods: Bool = false {
            didSet {
                update()
            }
        }

        weak var delegate: PaymentOptionCellDelegate? = nil

        // MARK: - UICollectionViewCell

        override init(frame: CGRect) {
            super.init(frame: frame)

            layer.shadowColor = UIColor.black.cgColor
            layer.shadowOpacity = PaymentSheetUI.defaultShadowOpacity
            layer.shadowRadius = PaymentSheetUI.defaultShadowRadius
            layer.shadowOffset = CGSize(width: 0, height: 1)

            [paymentMethodLogo, plus, selectedIcon].forEach {
                shadowRoundedRectangle.addSubview($0)
                $0.translatesAutoresizingMaskIntoConstraints = false
            }

            isAccessibilityElement = true
            paymentMethodLogo.contentMode = .scaleAspectFit
            deleteButton.addTarget(self, action: #selector(didSelectDelete), for: .touchUpInside)
            let views = [
                label, shadowRoundedRectangle, paymentMethodLogo, plus, selectedIcon, deleteButton,
            ]
            views.forEach {
                $0.translatesAutoresizingMaskIntoConstraints = false
                contentView.addSubview($0)
            }
            NSLayoutConstraint.activate([
                shadowRoundedRectangle.topAnchor.constraint(equalTo: contentView.topAnchor),
                shadowRoundedRectangle.leftAnchor.constraint(equalTo: contentView.leftAnchor),
                shadowRoundedRectangle.rightAnchor.constraint(equalTo: contentView.rightAnchor),
                shadowRoundedRectangle.widthAnchor.constraint(
                    equalToConstant: roundedRectangleSize.width),
                shadowRoundedRectangle.heightAnchor.constraint(
                    equalToConstant: roundedRectangleSize.height),

                label.topAnchor.constraint(
                    equalTo: shadowRoundedRectangle.bottomAnchor, constant: 4),
                label.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
                label.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 2),
                label.rightAnchor.constraint(equalTo: contentView.rightAnchor),

                paymentMethodLogo.centerXAnchor.constraint(
                    equalTo: shadowRoundedRectangle.centerXAnchor),
                paymentMethodLogo.centerYAnchor.constraint(
                    equalTo: shadowRoundedRectangle.centerYAnchor),
                paymentMethodLogo.widthAnchor.constraint(
                    equalToConstant: paymentMethodLogoSize.width),
                paymentMethodLogo.heightAnchor.constraint(
                    equalToConstant: paymentMethodLogoSize.height),

                plus.centerXAnchor.constraint(equalTo: shadowRoundedRectangle.centerXAnchor),
                plus.centerYAnchor.constraint(equalTo: shadowRoundedRectangle.centerYAnchor),
                plus.widthAnchor.constraint(equalToConstant: 32),
                plus.heightAnchor.constraint(equalToConstant: 32),

                selectedIcon.widthAnchor.constraint(equalToConstant: 26),
                selectedIcon.heightAnchor.constraint(equalToConstant: 26),
                selectedIcon.trailingAnchor.constraint(
                    equalTo: shadowRoundedRectangle.trailingAnchor, constant: 6),
                selectedIcon.bottomAnchor.constraint(
                    equalTo: shadowRoundedRectangle.bottomAnchor, constant: 6),

                deleteButton.trailingAnchor.constraint(
                    equalTo: shadowRoundedRectangle.trailingAnchor, constant: 6),
                deleteButton.topAnchor.constraint(
                    equalTo: shadowRoundedRectangle.topAnchor, constant: -6),
            ])
        }

        override func layoutSubviews() {
            super.layoutSubviews()
            layer.shadowPath = CGPath(ellipseIn: selectedIcon.frame, transform: nil)
        }

        required init?(coder: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }

        override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
            update()
        }

        override var isSelected: Bool {
            didSet {
                update()
            }
        }

        // MARK: - Internal Methods

        func setViewModel(_ viewModel: SavedPaymentOptionsViewController.Selection) {
            paymentMethodLogo.isHidden = false
            plus.isHidden = true
            shadowRoundedRectangle.isHidden = false
            self.viewModel = viewModel
            switch viewModel {
            case .saved(paymentMethod: _, label: let text, let image):
                label.text = text
                paymentMethodLogo.image = image
            case .applePay:
                // TODO (cleanup) - get this from PaymentOptionDisplayData?
                label.text = STPLocalizedString("Apple Pay", "Text for Apple Pay payment method")
                paymentMethodLogo.image = PaymentOption.applePay.makeCarouselImage()
            case .add:
                label.text = STPLocalizedString(
                    "+ Add",
                    "Text for a button that, when tapped, displays another screen where the customer can add payment method details"
                )
                paymentMethodLogo.isHidden = true
                plus.isHidden = false
                plus.setNeedsDisplay()
            }
            update()
        }

        func handleEvent(_ event: STPEvent) {
            UIView.animate(withDuration: PaymentSheetUI.defaultAnimationDuration) {
                switch event {
                case .shouldDisableUserInteraction:
                    self.label.alpha = 0.6
                case .shouldEnableUserInteraction:
                    self.label.alpha = 1
                default:
                    break
                }
            }
        }

        // MARK: - Private Methods
        @objc
        private func didSelectDelete() {
            delegate?.paymentOptionCellDidSelectRemove(self)
        }

        private func update() {
            let applyDefaultStyle: () -> Void = { [self] in
                shadowRoundedRectangle.isEnabled = true
                label.textColor = CompatibleColor.label
                paymentMethodLogo.alpha = 1
                plus.alpha = 1
                selectedIcon.isHidden = true
                layer.shadowOpacity = 0
                // Draw a outline in dark mode
                if #available(iOS 12.0, *) {
                    if traitCollection.userInterfaceStyle == .dark {
                        shadowRoundedRectangle.layer.borderWidth = 1
                        shadowRoundedRectangle.layer.borderColor =
                            CompatibleColor.systemGray4.cgColor
                    } else {
                        shadowRoundedRectangle.layer.borderWidth = 0
                    }
                }
            }

            if isRemovingPaymentMethods {

                if case .saved(let paymentMethod, _, _) = viewModel,
                    paymentMethod.isDetachableInPaymentSheet
                {
                    deleteButton.isHidden = false
                    contentView.bringSubviewToFront(deleteButton)
                    applyDefaultStyle()
                } else {
                    deleteButton.isHidden = true

                    // apply disabled style
                    shadowRoundedRectangle.isEnabled = false
                    paymentMethodLogo.alpha = 0.6
                    plus.alpha = 0.6
                    label.textColor = STPInputFormColors.disabledTextColor
                    // Draw a outline in dark mode
                    if #available(iOS 12.0, *) {
                        if traitCollection.userInterfaceStyle == .dark {
                            shadowRoundedRectangle.layer.borderWidth = 1
                            shadowRoundedRectangle.layer.borderColor =
                                CompatibleColor.systemGray4.cgColor
                        } else {
                            shadowRoundedRectangle.layer.borderWidth = 0
                        }
                    }
                }

            } else if isSelected {
                deleteButton.isHidden = true
                shadowRoundedRectangle.isEnabled = true
                label.textColor = CompatibleColor.label
                paymentMethodLogo.alpha = 1
                plus.alpha = 1
                selectedIcon.isHidden = false
                layer.shadowOpacity = PaymentSheetUI.defaultShadowOpacity

                // Draw a green border
                shadowRoundedRectangle.layer.borderWidth = 2
                shadowRoundedRectangle.layer.borderColor = UIColor.systemGreen.cgColor
            } else {
                deleteButton.isHidden = true
                shadowRoundedRectangle.isEnabled = true
                applyDefaultStyle()
            }
            accessibilityLabel = label.text
            accessibilityTraits = isSelected && !isRemovingPaymentMethods ? [.selected] : []
        }

    }

    // A circle with an image in the middle
    class CircleIconView: UIView {
        let imageView: UIImageView

        required init(icon: Icon) {
            imageView = UIImageView(image: icon.makeImage())
            super.init(frame: .zero)

            // Set colors according to the icon
            switch icon {
            case .plus:
                imageView.tintColor = CompatibleColor.secondaryLabel
                backgroundColor = UIColor.dynamic(
                    light: CompatibleColor.systemGray5, dark: CompatibleColor.tertiaryLabel)
            case .checkmark:
                imageView.tintColor = .white
                backgroundColor = .systemGreen
            default:
                break
            }

            addSubview(imageView)
            imageView.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                imageView.centerXAnchor.constraint(equalTo: centerXAnchor),
                imageView.centerYAnchor.constraint(equalTo: centerYAnchor),
            ])
            layer.masksToBounds = true
        }

        required init?(coder: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }

        override func layoutSubviews() {
            super.layoutSubviews()
            layer.cornerRadius = frame.width / 2
        }
    }
}
