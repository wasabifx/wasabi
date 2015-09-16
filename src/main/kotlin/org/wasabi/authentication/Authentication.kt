package org.wasabi.authentication

import org.wasabi.http.Request
import org.wasabi.http.Response

/**
 * Created by cnwdaa1 on 15/09/2015.
 */
public interface Authentication {
    fun authenticate(request: Request, response: Response) : Boolean
}