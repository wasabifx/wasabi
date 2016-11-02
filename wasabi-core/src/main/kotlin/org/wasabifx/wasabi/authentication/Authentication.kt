package org.wasabifx.wasabi.authentication

import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response

/**
 * Created by cnwdaa1 on 15/09/2015.
 */
interface Authentication {
    fun authenticate(request: Request, response: Response) : Boolean
}