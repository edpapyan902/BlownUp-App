<?php

namespace App\Http\Controllers\API;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;

//Models
use App\Models\Help;

class HelpController extends Controller {

    public function getHelp() {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $data['help'] = Help::get();
        $success = true;

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }
}
