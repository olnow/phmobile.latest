package olnow.phmobile.restapi;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
class ADStateSyncJob implements Job {
    private Logger logger = LoggerFactory.getLogger(ADStateSyncJob.class);

    @Autowired
    private
    ADUtils adUtils;

    @Override
    public void execute(JobExecutionContext context) {
        logger.info("Job ** {} ** starting @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());
        adUtils.syncADState();
        logger.info("Job ** {} ** completed.  Next job scheduled @ {}", context.getJobDetail().getKey().getName(), context.getNextFireTime());
    }
}
