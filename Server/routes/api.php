<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the 'api' middleware group. Enjoy building your API!
|
*/
Route::post( 'login', 'API\UserController@login' );
Route::post( 'signup', 'API\UserController@signup' );
Route::post( 'password/forget', 'API\UserController@forgotPassword' );
Route::post( 'password/reset', 'API\UserController@resetPassword' );

Route::group( ['middleware' => 'auth:api'], function () {

    //  Account
    Route::post( 'account/get_user', 'API\UserController@getUserInfo' );
    Route::post( 'account/update_device_token', 'API\UserController@updateDeviceToken' );
    Route::post( 'account/update', 'API\UserController@updateAccount' );

    //  Charge
    Route::post( 'charge', 'API\CheckoutController@charge' );
    Route::get( 'charge/status', 'API\CheckoutController@getChargeStatus' );
    
    //  Contact
    Route::get( 'contact', 'API\ContactController@getContact' );
    Route::post( 'contact/add', 'API\ContactController@addContact' );
    Route::post( 'contact/update', 'API\ContactController@updateContact' );
    Route::post( 'contact/delete', 'API\ContactController@deleteContact' );

    //  Schedule Call
    Route::get( 'schedule', 'API\ScheduleController@getSchedule' );
    Route::post( 'schedule/add', 'API\ScheduleController@addSchedule' );
    Route::post( 'schedule/update', 'API\ScheduleController@updateSchedule' );
    Route::post( 'schedule/delete', 'API\ScheduleController@deleteSchedule' );

    //  Help
    Route::get( 'help', 'API\HelpController@getHelp' );
});