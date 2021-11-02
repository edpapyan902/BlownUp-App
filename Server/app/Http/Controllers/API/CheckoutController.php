<?php

namespace App\Http\Controllers\API;

use Illuminate\Support\Facades\Auth;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

use Exception;

//Models
use App\Models\User;
use App\Models\TransactionHistory;

class CheckoutController extends Controller {

    public function charge( Request $request ) {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        try {
            $stripe = new \Stripe\StripeClient(
                env( 'STRIPE_SECRET' )
            );

            $paymentIntent_create_res = $stripe->paymentIntents->create( [
                'amount' => 999,
                'currency' => 'usd',
                'payment_method' => $request->payment_method,
                'description' => 'Getting Yourself Blown Up'
            ] );

            $paymentIntent_confirm_res = $stripe->paymentIntents->confirm(
                $paymentIntent_create_res->id
            );

            $data['client_secret'] = $paymentIntent_confirm_res->client_secret;

            $transactionHistory = new TransactionHistory;
            $transactionHistory->n_id_user = Auth::user()->id;
            $transactionHistory->payment_id = $paymentIntent_confirm_res->id;
            $transactionHistory->amount = 9.99;
            $transactionHistory->description = 'Getting Yourself Blown Up';
            $transactionHistory->save();

            $success = true;
            $message = 'Thank you for your charging our app!';

        } catch ( \Throwable $th ) {
            $success = false;
            $message = $th->getMessage();
        }

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function getChargeStatus() {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        //Get Charge Status
        $charged_count = TransactionHistory::where( 'n_id_user', Auth::user()->id )->count();
        $data['charged'] = $charged_count > 0 ? true : false;

        $success = true;

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }
}