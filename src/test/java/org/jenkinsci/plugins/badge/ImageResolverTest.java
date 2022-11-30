package org.jenkinsci.plugins.badge;

import hudson.model.BallColor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.mockito.Spy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class ImageResolverTest {
    ImageResolver ImageTester;
    
    @Test
    void BallShouldBeDefault(){
        ImageTester = new ImageResolver();
        String s = "Hello";
        String style="ball-32x32";
        String status = "passing";
        String subject = "build";
        String colorName=null;
        String fileName=  BallColor.getImageOf("32x32");
        //StatusImage statusImage = new StatusImage(fileName);
        BallColor.values();
        BallColor ball = new BallColor();
        BallColor ball = mock(BallColor.class);
        ImageTester.getImage(BallColor.ABORTED_ANIME,style,status,subject, colorName, null, null);

    }

    @Test
    void testName() {
        
    }


}
