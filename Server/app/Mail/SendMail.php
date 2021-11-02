<?php

namespace App\Mail;

use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Mail\Mailable;
use Illuminate\Queue\SerializesModels;

class SendMail extends Mailable {
    use Queueable, SerializesModels;

    /**
    * Create a new message instance.
    *
    * @return void
    */

    public function __construct( $subject, $text ) {
        $this->subject = $subject;
        $this->text = $text;
    }

    /**
    * Build the message.
    *
    * @return $this
    */

    public function build() {
        return $this->from( 'BlownUp@blownup.co' )
        ->replyTo( 'NoReply@blownup.co' )
        ->subject( $this->subject )
        ->html( $this->text )
        ->view( 'sendmail' );
    }
}
