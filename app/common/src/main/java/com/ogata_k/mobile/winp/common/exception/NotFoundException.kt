package com.ogata_k.mobile.winp.common.exception

class NotFoundException(target: String, id: Long?) :
    Throwable("NotFound data : target=%s".format(target, if (id == null) "" else ", id=$id"))