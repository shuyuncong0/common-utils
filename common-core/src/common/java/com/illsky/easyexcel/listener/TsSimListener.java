package com.illsky.easyexcel.listener;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;

import com.illsky.easyexcel.pojo.TsSimEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TsSimListener extends AnalysisEventListener<TsSimEntity> {

    // private ITsSimService tsSimService;
    private final int BATCH_COUNT = 500;
    protected final static Logger logger = LoggerFactory.getLogger(TsSimListener.class);
    private int count = 0;
    long[] arrId;
    private List<TsSimEntity> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    List<Future<?>> futureList =  ListUtils.newArrayListWithExpectedSize(2000);

    public TsSimListener() {}

    // 通过构造方法传入 tsSimService
    // public TsSimListener(ITsSimService tsSimService) {
    //     this.tsSimService = tsSimService;
    // }

    // 监听器每次读取一行数据都会调用invoke方法
    @Override
    public void invoke(TsSimEntity tsSimEntity, AnalysisContext analysisContext) {
        /*tsSimService.getDataCount();
        if (arrId != null && arrId.length > 0) {
            // 如果ID数组中有值则将ID赋值给实体
            tsSimEntity.setId(arrId[count++]);

        } else {
            // 如果ID数组中没有值则获取ID数组
            arrId = tsSimService.getArrId();
            // 如果ID数组中有值则将ID赋值给实体
            tsSimEntity.setId(arrId[count++]);
        }
        tsSimEntity.setIccid(tsSimEntity.getIccid().trim());
        tsSimEntity.setCreateon(new Date());
        // 同步流向
        tsSimEntity.setIssync(1);

        if (StringUtils.isNotBlank(tsSimEntity.getOnelinkstate())){
            if (SimSystemConst.SimStateDesc.NORMAL.equals(tsSimEntity.getOnelinkstate().trim())
                    || SimSystemConst.SimStateDesc.ACTIVATION.equals(tsSimEntity.getOnelinkstate().trim())) {
                tsSimEntity.setState(SimSystemConst.SimState.NORMAL);
            } else {
                tsSimEntity.setState(SimSystemConst.SimState.ABNORMAL);
            }
        }
        // 未知异常
        tsSimEntity.setPackagestate("2");
        // 未绑定
        tsSimEntity.setBindingstate("0");
        tsSimEntity.setStatemodifiedon(new Date());
        tsSimEntity.setPackagestatemodifiedon(new Date());

        SessionUserBean sessionUserBean = ThreadLocalContext.sessionUserBeanThreadLocal.get();
        tsSimEntity.setCreateuser(sessionUserBean.getUser().getId());

        cachedDataList.add(tsSimEntity);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理ID数组及计数器
            arrId = null;
            count = 0;
        }*/
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        // 校验是否完成
        checkComplete();
    }

    private void saveData() {
        logger.info("{}条数据，开始存储数据库！", cachedDataList.size());
        List<TsSimEntity> tempList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        CollectionUtils.addAll(tempList, cachedDataList);
        cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        Future<?> future = ThreadUtil.execAsync(() -> {
            // tsSimService.insertBatch(tempList);
        });
        futureList.add(future);
    }

    /**
     * @author: sucongcong
     * @date: 2023/5/16
     * @param: []
     * @return: void
     * @description: 校验是否完成
     * @modify:
     */
    private void checkComplete() {
        for (Future<?> future : futureList) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private boolean checkData(TsSimEntity tsSimEntity) {
        String regex = "[0-9a-zA-Z]{20}";
        return tsSimEntity.getIccid().matches(regex);
    }
}
