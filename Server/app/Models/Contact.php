<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Contact extends Model {
    protected $table = 'contact';
    protected $fillable = [
        'n_id_user',
        'name',
        'avatar',
        'number',
        'created_at',
        'updated_at'
    ];
}
