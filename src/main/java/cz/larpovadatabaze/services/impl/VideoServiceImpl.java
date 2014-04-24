package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.VideoDAO;
import cz.larpovadatabaze.entities.Video;
import cz.larpovadatabaze.services.VideoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@Repository
public class VideoServiceImpl implements VideoService {

    public static class EmbededVideoURLTransform {
        /**
         * Lost of accepted patterns
         */
        private final Pattern pattern;

        /**
         * Format of the resulting URL
         */
        private final String urlFormat;

        public EmbededVideoURLTransform(Pattern pattern, String urlFormat) {
            this.pattern = pattern;
            this.urlFormat = urlFormat;
        }
    }

    @Autowired
    VideoDAO videoDAO;

    private EmbededVideoURLTransform[] embededVideoTransforms =  {
            new EmbededVideoURLTransform(Pattern.compile("//www\\.youtube\\.com/embed/([^?/]+)"), "//www.youtube.com/embed/$1"),
            new EmbededVideoURLTransform(Pattern.compile("//www\\.youtube\\.com/watch\\?v=([^&]+)"), "//www.youtube.com/embed/$1"),
            new EmbededVideoURLTransform(Pattern.compile("//youtu\\.be/([^?/]+)"), "//www.youtube.com/embed/$1"),
    };

    @Override
    public List<Video> getAll() {
        return videoDAO.findAll();
    }

    @Override
    public List<Video> getUnique(Video example) {
        return videoDAO.findByExample(example, new String[]{});
    }

    @Override
    public void remove(Video toRemove) {
        videoDAO.makeTransient(toRemove);
    }

    @Override
    public List<Video> getFirstChoices(String startsWith, int maxChoices) {
        throw new UnsupportedOperationException("This does not support autocompletion");
    }

    @Override
    public boolean saveOrUpdate(Video video) {
        return videoDAO.saveOrUpdate(video);
    }

    @Override
    public String getEmbedingURL(String url) {
        if (StringUtils.isBlank(url)) return "";

        for(EmbededVideoURLTransform transform : embededVideoTransforms) {
            // Check if URL matches this transform
            Matcher m = transform.pattern.matcher(url);
            if (m.find()) {
                // Found - use
                StringBuffer sb = new StringBuffer();
                m.appendReplacement(sb, transform.urlFormat);
                return sb.toString().substring(m.start()); // We have to cut off beginning which appendReplacement() "conveniently" appends
            }
        }

        return null;
    }
}
