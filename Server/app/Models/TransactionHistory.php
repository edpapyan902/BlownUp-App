<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class TransactionHistory extends Model {
    protected $table = 'transaction_history';
    protected $fillable = [
        'n_id_user',
        'payment_id',
        'amount',
        'description',
        'created_at',
        'updated_at'
    ];
}
