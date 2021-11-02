<?php

namespace App\Http\Controllers\API;

use Illuminate\Support\Facades\Auth;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

//Models
use App\Models\Contact;
use App\Models\Schedule;
use App\Models\User;

class ContactController extends Controller {

    private $default_avatar_url = '/material/img/default_avatar.png';

    public function getContact() {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $contacts = Contact::where( 'n_id_user', Auth::user()->id )->get();
        $data['contacts'] = $contacts;

        $success = true;

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function addContact( Request $request ) {

        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $name = $request->name;
        $number = $request->number;
        $avatar_base64 = $request['avatar'];

        $exists = Contact::where( ['n_id_user' => Auth::user()->id, 'number' => $number] )->count();
        if ( $exists > 0 ) {
            $message = 'This phone number already is on your contact list.';
            $success = false;
        } else {
            $dest_path = $this->default_avatar_url;
            if ( $avatar_base64 != '' ) {
                //contact image upload
                $targetDir = base_path( 'uploads' );
                if ( !is_dir( $targetDir ) ) {
                    mkDir( $targetDir, 0777, true );
                }
                $targetDir .= '/contact';
                if ( !is_dir( $targetDir ) ) {
                    mkDir( $targetDir, 0777, true );
                }

                $dest_file_name = $this->getUniqueString().'.png';
                $dest_path = '/uploads/contact/'.$dest_file_name;
                $targetDir .= '/'.$dest_file_name;

                file_put_contents( $targetDir, base64_decode( $avatar_base64 ) );
                //contact image upload
            }

            $newContact = new Contact;
            $newContact->n_id_user = Auth::user()->id;
            $newContact->name = $request->name;
            $newContact->avatar = $dest_path;
            $newContact->number = $request->number;
            $newContact->save();

            $message = 'Successfully added on your contact list.';
            $success = true;
        }

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function updateContact( Request $request ) {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $id = $request->id;
        $name = $request->name;
        $number = $request->number;
        $avatar_base64 = $request['avatar'];

        $base_number = Contact::where( 'id', $id )->value( 'number' );

        $exists = Contact::where( ['n_id_user' => Auth::user()->id, 'number' => $number] )->count();
        if ( $exists > 0 && $base_number != $number ) {
            $message = 'This phone number already is on your contact list.';
            $success = false;
        } else {
            if ( $avatar_base64 != '' ) {
                //remove image
                $contact_avatar = Contact::where( 'id', $id )->value( 'avatar' );
                if ( $contact_avatar != '' && $contact_avatar != $this->default_avatar_url ) {
                    $file_path = substr( $contact_avatar, 1 );
                    if ( file_exists( $file_path ) ) {
                        unlink( $file_path );
                    }
                }
                //contact image upload
                $targetDir = base_path( 'uploads' );
                if ( !is_dir( $targetDir ) ) {
                    mkDir( $targetDir, 0777, true );
                }
                $targetDir .= '/contact';
                if ( !is_dir( $targetDir ) ) {
                    mkDir( $targetDir, 0777, true );
                }

                $dest_file_name = $this->getUniqueString().'.png';
                $dest_path = '/uploads/contact/'.$dest_file_name;
                $targetDir .= '/'.$dest_file_name;

                file_put_contents( $targetDir, base64_decode( $avatar_base64 ) );
                //contact image upload

                Contact::where( 'id', $id )->update( ['avatar' => $dest_path] );
            }

            Contact::where( 'id', $id )->update( ['name' => $name, 'number' => $number, 'updated_at' => now()] );

            $success = true;
            $message = 'Successfully updated.';
        }
        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function deleteContact( Request $request ) {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $id = $request->id;

        $exists = Schedule::where( 'n_id_contact', $id )->count();
        if ( $exists == 0 ) {
            $contact_avatar = Contact::where( 'id', $id )->value( 'avatar' );
            if ( $contact_avatar != '' && $contact_avatar != $this->default_avatar_url ) {
                $file_path = substr( $contact_avatar, 1 );
                if ( file_exists( $file_path ) ) {
                    unlink( $file_path );
                }
            }

            Contact::where( 'id', $id )->delete();

            $success = true;
            $message = 'Successfully removed on your contact list.';
        } else {
            $success = false;
            $message = "This contact can't delete. Some calls scheduled from this contact.";
        }
        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    function getUniqueString() {
        return md5( uniqid( mt_rand(), true ) );
    }
}
