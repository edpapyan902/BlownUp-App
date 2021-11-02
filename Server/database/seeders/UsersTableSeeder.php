<?php
namespace Database\Seeders;

use Illuminate\Support\Facades\DB;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class UsersTableSeeder extends Seeder {
    /**
    * Run the database seeds.
    *
    * @return void
    */

    public function run() {
        DB::table( 'users' )->insert( [
            'name' => 'Michael Niemis',
            'email' => 'blownup@blownup.co',
            'email_verified_at' => now(),
            'password' => Hash::make( 'blownup' ),
            'spoof_phone_number' => '+1 813-502-0487',
            'role' => 1,
            'terms' => 1,
            'created_at' => now(),
            'updated_at' => now()
        ] );
    }
}