<?php

namespace App\Http\Controllers\API;

use Illuminate\Support\Facades\Auth;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

use Exception;

//Models
use App\Models\User;

class NotificationController extends Controller {

    public function send( $user_id, $type, $title, $body, $image, $data ) {

        $device_token = User::where( 'id', $user_id )->value( 'device_token' );

        $notification_data = array
        (
            'title' => $title,
            'body' => $body,
            'data' => $data,
            'type' => $type,
            'badge' => 1,
            'sound' => 'default',
            'image' => $image
        );

        $cloud_message_data_android = array
        (
            'to' => $device_token,
            'data' => $notification_data,
            'notification' => $notification_data,
            'priority' => 'high'
        );

        $data_string_android = json_encode( $cloud_message_data_android );

        $result = array();
        $result['android'] = $this->sendFCM( $data_string_android );

        return $result;
    }

    public function sendDataOnly( $user_id, $type, $data ) {

        $device_token = User::where( 'id', $user_id )->value( 'device_token' );

        $notification_data = array
        (
            'data' => $data,
            'type' => $type
        );

        $cloud_message_data_android = array
        (
            'to' => $device_token,
            'data' => $notification_data,
            'priority' => 'high'
        );

        $data_string_android = json_encode( $cloud_message_data_android );

        $result = array();
        $result['android'] = $this->sendFCM( $data_string_android );

        return $result;
    }

    public function sendFCM( $data_string ) {
        $url = 'https://fcm.googleapis.com/fcm/send';
        $headers = array
        (
            'Authorization: key=' . env( 'FCM_SERVER_API_KEY' ),
            'Content-Type: application/json'
        );

        $ch = curl_init();
        curl_setopt( $ch, CURLOPT_URL, $url );
        curl_setopt( $ch, CURLOPT_POST, true );
        curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers );
        curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
        curl_setopt( $ch, CURLOPT_SSL_VERIFYPEER, false );
        curl_setopt( $ch, CURLOPT_POSTFIELDS, $data_string );

        $result = curl_exec( $ch );
        curl_close( $ch );

        return $result;
    }

    public function sentVoipNotification( $user_id, $name, $phoneNumber, $avatar ) {
        $iphone_device_token = User::where( 'id', $user_id )->value( 'iphone_device_token' );

        $aps_data = array
        (
            'content-available' => 1
        );

        $voip_data = array
        (
            'aps'=> $aps_data,
            'name' => $name,
            'phoneNumber' => $phoneNumber,
            'avatar' => $avatar
        );

        $status = $this->sendVoipPush( $iphone_device_token, json_encode( $voip_data ) );

        return $status;
    }

    public function sendVoipPush( $token, $data_string ) {
        $url = "https://api.push.apple.com/3/device/{$token}";

        $voip_cert = __DIR__.'/voip_cert.pem';

        $headers = array(
            'apns-priority' => 10
        );

        $ch = curl_init();
        curl_setopt( $ch, CURLOPT_URL, $url );
        curl_setopt( $ch, CURLOPT_PORT, 443 );
        curl_setopt( $ch, CURLOPT_POST, true );
        curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers );
        curl_setopt( $ch, CURLOPT_HEADER, true );
        curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
        curl_setopt( $ch, CURLOPT_SSL_VERIFYPEER, false );
        curl_setopt( $ch, CURLOPT_SSLCERT, $voip_cert );
        curl_setopt( $ch, CURLOPT_POSTFIELDS, $data_string );

        $result = curl_exec( $ch );
        if ( $result === FALSE ) {
            throw new Exception( 'Curl failed with error: ' . curl_error( $ch ) );
        }

        $status = curl_getinfo( $ch, CURLINFO_HTTP_CODE );
        curl_close( $ch );

        return $result;
    }

    public function testVoip() {
        $status = $this->sentVoipNotification( 6, 'Test Caller', '32456789', '' );
        return $status;
    }
}