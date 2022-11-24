package org.jenkinsci.plugins.badge;

import hudson.model.BallColor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import static org.hamcrest.MatcherAssert.assertThat;

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
        ImageTester.getImage(BallColor.BLUE,style,status,subject, colorName, null, null);

    }


}
