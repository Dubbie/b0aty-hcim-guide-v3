package com.b0atyguide.data;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GuideLoaderTest
{
    @Test
    public void bundledGuideLoadsSuccessfully() throws Exception
    {
        Guide guide = new GuideLoader(new Gson()).loadBundled();

        assertNotNull(guide);
    }
}
