<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;

use App\Http\Controllers\API\NotificationController;

//  Models
use App\Models\Schedule;

class ScheduleCallCronJob extends Command {
    /**
    * The name and signature of the console command.
    *
    * @var string
    */
    protected $signature = 'ScheduleCallCronJob:CheckScheduleCall';

    /**
    * The console command description.
    *
    * @var string
    */
    protected $description = 'Scheduled Call';

    /**
    * Create a new command instance.
    *
    * @return void
    */

    public function __construct() {
        parent::__construct();
    }

    /**
    * Execute the console command.
    *
    * @return int
    */

    public function handle() {
        $today = date( 'Y-m-d H:i' );
        $today .= ':00';

        $notificationController = new NotificationController;
        $schedules = Schedule::where( 'scheduled_at', $today )->get();
        foreach ( $schedules as $key => $item ) {
            $item->contact;

            $number = $item['number'];
            $name = '';
            $avatar = '';
            if ( $number == null || $number == '' ) {
                $name = $item['contact']['name'];
                $number = $item['contact']['number'];
                $avatar = $item['contact']['avatar'];
            }

            //  Remove Schedule
            Schedule::where( 'id', $item->id )->delete();

            //  Send Voip Push
            $notificationController->sentVoipNotification( $item->n_id_user, $name, $number, $avatar );
        }

        return 0;
    }
}
