package routing


import org.junit.Test as spec
import kotlin.test.assertEquals
import org.wasabi.routing.UrlParameterParser


public class UrlParameterParserSpecs {

    spec fun given_an_empty_url_the_list_of_parameters_should_be_empty() {

        val urlParameterParser = UrlParameterParser()

        val urlParams = urlParameterParser.parseUrlPattern("")

        assertEquals(0, urlParams.size())

    }

    spec fun given_a_url_with_a_single_parameter_it_should_return_a_list_of_parameters_of_one_entry_corresponding_to_the_parameter() {

        val urlParameterParser = UrlParameterParser()

        val urlParams = urlParameterParser.parseUrlPattern("/customer/:id")

        assertEquals(1, urlParams.size())
        assertEquals("id", urlParams[0])
    }
}