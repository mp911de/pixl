package pixl.application;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Data
public class Playlist {
    private List<PlaylistItem> items = Lists.newArrayList();

    public PlaylistItem getItem(int index) {
        return items.get(index);
    }

    public int size() {
        return items.size();
    }
}
