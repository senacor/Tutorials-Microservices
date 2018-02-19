package com.senacor.bitc.demo.util;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;

public class TestUtil {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8")
    );

    public static final MediaType HAL_JSON_UTF8 = new MediaType(MediaTypes.HAL_JSON.getType(),
            MediaTypes.HAL_JSON.getSubtype(),
            Charset.forName("utf8")
    );
}
