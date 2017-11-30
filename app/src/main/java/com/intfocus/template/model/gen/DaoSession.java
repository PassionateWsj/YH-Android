package com.intfocus.template.model.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.intfocus.template.model.entity.Collection;
import com.intfocus.template.model.entity.Source;
import com.intfocus.template.model.entity.Report;

import com.intfocus.template.model.gen.CollectionDao;
import com.intfocus.template.model.gen.SourceDao;
import com.intfocus.template.model.gen.ReportDao;

import com.intfocus.template.model.gen.CollectionDao;
import com.intfocus.template.model.gen.ReportDao;
import com.intfocus.template.model.gen.SourceDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig collectionDaoConfig;
    private final DaoConfig sourceDaoConfig;
    private final DaoConfig reportDaoConfig;

    private final CollectionDao collectionDao;
    private final SourceDao sourceDao;
    private final ReportDao reportDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        collectionDaoConfig = daoConfigMap.get(CollectionDao.class).clone();
        collectionDaoConfig.initIdentityScope(type);

        sourceDaoConfig = daoConfigMap.get(SourceDao.class).clone();
        sourceDaoConfig.initIdentityScope(type);

        reportDaoConfig = daoConfigMap.get(ReportDao.class).clone();
        reportDaoConfig.initIdentityScope(type);

        collectionDao = new CollectionDao(collectionDaoConfig, this);
        sourceDao = new SourceDao(sourceDaoConfig, this);
        reportDao = new ReportDao(reportDaoConfig, this);

        registerDao(Collection.class, collectionDao);
        registerDao(Source.class, sourceDao);
        registerDao(Report.class, reportDao);
    }
    
    public void clear() {
        collectionDaoConfig.clearIdentityScope();
        sourceDaoConfig.clearIdentityScope();
        reportDaoConfig.clearIdentityScope();
    }

    public CollectionDao getCollectionDao() {
        return collectionDao;
    }

    public SourceDao getSourceDao() {
        return sourceDao;
    }

    public ReportDao getReportDao() {
        return reportDao;
    }

}
