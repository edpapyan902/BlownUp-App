<?php

namespace App\Http\Controllers\API;
use App\Http\Controllers\Controller;

use App\Mail\SendMail;
use Illuminate\Support\Facades\Mail;

//  Models
use App\Models\EmailHistory;
use App\Models\User;

class EmailController extends Controller {

    public function sendVerifyCode( $email, $code ) {
        $message = 'Your verification code is '.$code;
        $subject = 'You have received verification code!';
        $this->sendBasicMail( $email, $subject, $message );
    }

    public function sendBasicMail( $email, $subject, $message ) {
        Mail::to( $email )->send( new SendMail( $subject, $message ) );
    }
}