<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Schedule extends Model {
    protected $table = 'schedule';
    protected $fillable = [
        'n_id_user',
        'n_id_contact',
        'number',
        'scheduled_at',
        'alarm_identify',
        'created_at',
        'updated_at'
    ];

    public function contact() {
        return $this->hasOne( 'App\Models\Contact', 'id', 'n_id_contact' );
    }
}
