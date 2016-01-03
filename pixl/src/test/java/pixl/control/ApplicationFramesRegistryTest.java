package pixl.control;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import pixl.application.ApplicationFrames;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class ApplicationFramesRegistryTest {

    private ApplicationRegistry sut;

    @Before
    public void before() throws Exception {
        sut = new ApplicationRegistry();
        sut.setResourceLoader(new DefaultResourceLoader());
    }

    @Test
    public void testPostConstruct() throws Exception {

        sut.postConstruct();
        ApplicationFrames time = sut.getApplicationFrames("time");
        assertThat(time).isNotNull();
        assertThat(time.getFrames()).hasSize(1);
    }

    @Test
    public void testPostConstructTwice() throws Exception {

        sut.postConstruct();
        sut.postConstruct();
        ApplicationFrames time = sut.getApplicationFrames("time");
        assertThat(time).isNotNull();
        assertThat(time.getFrames()).hasSize(1);
    }

    @Test
    public void testNested() throws Exception {

        sut.postConstruct();
        assertThat(sut.getApplicationFrames("dummy")).isNotNull();

    }
}