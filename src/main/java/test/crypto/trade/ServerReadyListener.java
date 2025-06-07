package test.crypto.trade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import test.crypto.trade.entity.User;
import test.crypto.trade.entity.Wallet;
import test.crypto.trade.repository.UserRepository;
import test.crypto.trade.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ServerReadyListener implements ApplicationListener<WebServerInitializedEvent> {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        System.out.println("Server is ready and listening on port: " + port);

        List<Wallet> init = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUsername("username" + i);
            user.setEmail("email" + i);
            user = userRepository.save(user);
            // Run scrip to add Wallet
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            wallet.setCurrency("USDT");
            wallet.setBalance(BigDecimal.valueOf(50000));

            init.add(wallet);
        }

        walletRepository.saveAllAndFlush(init);
    }
}