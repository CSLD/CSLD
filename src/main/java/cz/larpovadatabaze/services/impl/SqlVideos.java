package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.entities.Video;
import cz.larpovadatabaze.services.Videos;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Videos stored in the SQL Data Store.
 */
@Repository
@Transactional
public class SqlVideos extends CRUD<Video, Integer> implements Videos {

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

    public SqlVideos(SessionFactory sessionFactory) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Video.class)));
    }

    private EmbededVideoURLTransform[] embededVideoTransforms = {
            new EmbededVideoURLTransform(Pattern.compile("//www\\.youtube\\.com/embed/([^?/]+)"), "//www.youtube.com/embed/$1"),
            new EmbededVideoURLTransform(Pattern.compile("//www\\.youtube\\.com/watch\\?v=([^&]+)"), "//www.youtube.com/embed/$1"),
            new EmbededVideoURLTransform(Pattern.compile("//youtu\\.be/([^?/]+)"), "//www.youtube.com/embed/$1"),
    };

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
