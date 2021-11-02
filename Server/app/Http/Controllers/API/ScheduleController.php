<?php

namespace App\Http\Controllers\API;

use Illuminate\Support\Facades\Auth;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

use DB;

//Models
use App\Models\Schedule;
use App\Models\Contact;

class ScheduleController extends Controller {

    public function getSchedule() {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $schedules = Schedule::where( 'n_id_user', Auth::user()->id )->orderBy('scheduled_at', 'asc')->get();
        foreach ( $schedules as $key => $item ) {
            $item->contact;
        }

        $data['schedules'] = $schedules;
        $success = true;

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function addSchedule( Request $request ) {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $n_id_contact = $request->n_id_contact;
        $number = $request->number;
        $scheduled_at = $request->scheduled_at;

        $exist = 0;
        if ( $n_id_contact > 0 ) {
            $exist = Schedule::where( ['n_id_contact' => $n_id_contact, 'scheduled_at' => $scheduled_at] )->count();
        } else {
            $exist = Schedule::where( ['n_id_contact' => $n_id_contact, 'number' => $number, 'scheduled_at' => $scheduled_at] )->count();
        }

        if ( $exist > 0 ) {
            $success = false;
            $message = 'This number has already been scheduled.';
        } else {
            if ( $n_id_contact == 0 ) {
                $exist_on_contact = Contact::where( 'number', $number )->count();
                if ( $exist_on_contact > 0 ) {
                    //when phone number exists on contact.
                    $contact = Contact::where( 'number', $number )->first();
                    $n_id_contact = $contact->id;
                    $number = null;

                    //when contact exists on schedule.
                    $exist = Schedule::where( ['n_id_contact' => $n_id_contact, 'scheduled_at' => $scheduled_at] )->count();
                    if ( $exist > 0 ) {
                        $success = false;
                        $message = 'This number has already been scheduled.';

                        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
                    }
                }
            }

            $alarm_identify = $this->getUniqueString();

            $newSchedule = new Schedule;
            $newSchedule->n_id_user = Auth::user()->id;
            $newSchedule->n_id_contact = $n_id_contact;
            $newSchedule->number = $number;
            $newSchedule->scheduled_at = $scheduled_at;
            $newSchedule->alarm_identify = $alarm_identify;
            $newSchedule->save();

            $success = true;
            $message = 'Successfully scheduled.';

            $newSchedule->contact;
            $data['schedule'] = $newSchedule;
        }

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function updateSchedule( Request $request ) {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        $id = $request->id;
        $n_id_contact = $request->n_id_contact;
        $number = $request->number;
        $scheduled_at = $request->scheduled_at;

        $exist = 0;
        if ( $n_id_contact > 0 ) {
            $exist = Schedule::where( ['n_id_contact' => $n_id_contact, 'scheduled_at' => $scheduled_at] )->count();
        } else {
            $exist = Schedule::where( ['n_id_contact' => $n_id_contact, 'number' => $number, 'scheduled_at' => $scheduled_at] )->count();
        }

        if ( $exist > 1 ) {
            $success = false;
            $message = 'This number has already been scheduled.';
        } else {
            if ( $n_id_contact == 0 ) {
                $exist_on_contact = Contact::where( 'number', $number )->count();
                if ( $exist_on_contact > 0 ) {
                    //when phone number exists on contact.
                    $contact = Contact::where( 'number', $number )->first();
                    $n_id_contact = $contact->id;
                    $number = null;

                    //when contact exists on schedule.
                    $exist = Schedule::where( ['n_id_contact' => $n_id_contact, 'scheduled_at' => $scheduled_at] )->count();
                    if ( $exist > 0 ) {
                        $schedule_id = Schedule::where( ['n_id_contact' => $n_id_contact, 'scheduled_at' => $scheduled_at] )->value( 'id' );
                        if ( $id != $schedule_id ) {
                            $success = false;
                            $message = 'This number has already been scheduled.';

                            return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
                        }
                    }
                }
            }

            $old_alarm_identify = Schedule::where( 'id', $id )->value( 'alarm_identify' );
            $data['old_alarm_identify'] = $old_alarm_identify;
            Schedule::where( 'id', $id )->update( ['n_id_contact' => $n_id_contact, 'number' => $number, 'scheduled_at' => $scheduled_at, 'alarm_identify' => $this->getUniqueString(), 'updated_at' => now()] );

            $success = true;
            $message = 'Successfully updated.';

            $schedule = Schedule::where( 'id', $id )->first();
            $schedule->contact;
            $data['schedule'] = $schedule;
        }

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    public function deleteSchedule( Request $request ) {
        $data = array();
        $data['any'] = 'swiftonly';
        $success = false;
        $message = '';

        Schedule::where( 'id', $request->id )->delete();

        $success = true;
        $message = 'Successfully removed.';

        return $response = array( 'success' => $success, 'data' => $data, 'message' => $message );
    }

    function getUniqueString() {
        return md5( uniqid( mt_rand(), true ) );
    }
}
