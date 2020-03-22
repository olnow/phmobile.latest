package olnow.phmobile.restapi;

import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
class QuartzSubmitJobs {

    @Bean(name = "ADStateSync")
    public JobDetailFactoryBean jobMemberClassStats() {
        return QuartzConfig.createJobDetail(ADStateSyncJob.class, "AD State Sync Job");
    }

    @Bean(name = "memberClassStatsTrigger")
    public CronTriggerFactoryBean triggerMemberClassStats(@Qualifier("ADStateSync") JobDetail jobDetail) {
        return QuartzConfig.createCronTrigger(jobDetail, AppProperties.getStringValue("AD_SYNC_STATE_CRON"), "AD State Sync Trigger");
    }
}
