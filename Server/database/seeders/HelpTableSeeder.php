<?php

namespace Database\Seeders;

use Illuminate\Support\Facades\DB;
use Illuminate\Database\Seeder;

class HelpTableSeeder extends Seeder {
    /**
    * Run the database seeds.
    *
    * @return void
    */

    public function run() {
        DB::table( 'help' )->insert( [
            'type' => '0',
            'content' => '\material\img\help.mp4',
            'created_at' => now(),
            'updated_at' => now()
        ] );
        DB::table( 'help' )->insert( [
            'type' => '1',
            'content' => "To have your phone ringing with contacts you create, first go to the contacts tab and setup all your contacts. Enter any phone number you want the call to be from. The system runs on caller-id spoofing so the phone number doesn't matter.",
            'created_at' => now(),
            'updated_at' => now()
        ] );
        DB::table( 'help' )->insert( [
            'type' => '1',
            'content' => 'After you establish your contacts, use the Schedule a New Call feature at the top right of the app. Set the time and date you want the incoming call to take place. Schedule as many calls as you like.',
            'created_at' => now(),
            'updated_at' => now()
        ] );
        DB::table( 'help' )->insert( [
            'type' => '1',
            'content' => 'Scheduled calls will happen at the time and day of your choosing. Enjoy.',
            'created_at' => now(),
            'updated_at' => now()
        ] );
    }
}
