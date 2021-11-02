<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Help extends Model {
    protected $table = 'help';
    protected $fillable = [
        'type',
        'content',
        'created_at',
        'updated_at'
    ];
}
