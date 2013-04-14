package org.wasabi.http

public class ContentTypeParserLocator: ParserLocator {

    // use IoC here once done
    override fun locateParser(contentType: String): BodyParser? {
        when (contentType.toUpperCase()) {

            else -> {
                return null
            }
        }




    }
}