package org.wasabifx.authentication

import org.wasabifx.protocol.http.Request
import org.wasabifx.protocol.http.Response

/**
 * Created by cnwdaa1 on 15/09/2015.
 */
public interface Authentication {
    fun authenticate(request: Request, response: Response) : Boolean
}