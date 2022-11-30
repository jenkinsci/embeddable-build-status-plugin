package org.jenkinsci.plugins.badge;

import hudson.model.BallColor;
import hudson.model.Messages;
import hudson.util.ColorPalette;
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
        //StatusImage statusImage = new StatusImage(fileName);
       
        BallColor ball = mock(BallColor.class);
        Mockito.when(ball.getImageOf("32x32")).thenReturn("hello");
        


    }

    @Test
    void testName() {
        
    }


}
