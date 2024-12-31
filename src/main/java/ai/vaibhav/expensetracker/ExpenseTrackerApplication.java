package ai.vaibhav.expensetracker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.cfg.Environment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Slf4j
public class ExpenseTrackerApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(ExpenseTrackerApplication.class, args);
		try {
			ConfigurableEnvironment environment = ctx.getEnvironment();
			String[] activeProfiles = environment.getActiveProfiles();
			//if(List.of(activeProfiles).contains("prod")) {
				TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
				botsApi.registerBot(ctx.getBean("invoiceReaderBot", LongPollingBot.class));
			//}
		} catch (TelegramApiException e) {
			log.error("Telegram exception", e);
			throw new RuntimeException(e);
		}
	}

}
