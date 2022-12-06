package org.jenkinsci.plugins.badge;

import org.junit.ClassRule;
import hudson.model.BallColor;

import org.junit.Test;

import org.jvnet.hudson.test.JenkinsRule;

import org.mockito.Mockito;
<<<<<<< HEAD
=======

import static org.hamcrest.MatcherAssert.assertThat;
>>>>>>> 9483900f4867983ecdda36804c4700a97ff89f0a
import static org.mockito.Mockito.mock;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class ImageResolverTest {
    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void testGetDefault32x32Ball() throws Exception {
        ImageResolver ImageTester = new ImageResolver();
        String style="ball-null"; // should give default due to url being null
        String status = null;
        String subject = null;
        String colorName=null;
        String fileName = "images/32x32/blue.png";
        BallColor ball = mock(BallColor.class);
        //default
        Mockito.when(ball.getImageOf("null")).thenReturn(null);
        Mockito.when(ball.getImageOf("32x32")).thenReturn(fileName);
        Mockito.when(ball.noAnime()).thenCallRealMethod();
        StatusImage TestImage = ImageTester.getImage(ball, style,subject,status, colorName, null, null);
        assertThat(TestImage.getEtag(), containsString(fileName));

     
    }
    @Test
    public void testGetNonDefaultBall() throws Exception {
        ImageResolver ImageTester = new ImageResolver();
        String style="ball-16x16"; // should give url
        String status = null;
        String subject = null;
        String colorName=null;
        String fileName = "images/16x16/red.png";
        BallColor ball = mock(BallColor.class);
        //default
        Mockito.when(ball.getImageOf("null")).thenReturn(null); 
        Mockito.when(ball.getImageOf("16x16")).thenReturn(fileName);
        Mockito.when(ball.noAnime()).thenCallRealMethod();
        StatusImage TestImage = ImageTester.getImage(ball, style,subject,status, colorName, null, null);
        assertThat(TestImage.getEtag(), containsString(fileName));

     
    }
    @Test
    public void testShouldReturnEmpty() throws Exception {
        ImageResolver ImageTester = new ImageResolver();
        String style="ball-42x45"; // should throw exception
        String status = null;
        String subject = null;
        String colorName=null;
        String fileName = "DoesNotExist";;
        BallColor ball = mock(BallColor.class);
        //default
        Mockito.when(ball.getImageOf("null")).thenReturn(null);
        Mockito.when(ball.getImageOf("42x45")).thenReturn(fileName);
        Mockito.when(ball.noAnime()).thenCallRealMethod();
        StatusImage TestImage = ImageTester.getImage(ball, style,subject,status, colorName, null, null);
        assertThat(TestImage.getEtag(), containsString("empty"));

     
    }
}



