<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateScheduleTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('schedule', function (Blueprint $table) {
            $table->id();
            $table->integer('n_id_user');
            $table->integer('n_id_contact')->default(0)->comment('0:New, value > 0: Contact ID');
            $table->string('number')->nullable();
            $table->dateTime('scheduled_at');
            $table->string('alarm_identify')->comment('alarm schedule id');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('schedule');
    }
}
