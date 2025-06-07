package test.crypto.trade.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.crypto.trade.entity.User;
import test.crypto.trade.entity.Wallet;
import test.crypto.trade.model.Symbol;
import test.crypto.trade.repository.UserRepository;
import test.crypto.trade.repository.WalletRepository;
import test.crypto.trade.response.UserWalletResponse;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;


    public List<UserWalletResponse> getUserWallets(Long userId) {
        User user = userRepository.getReferenceById(userId);

        List<Wallet> wallets = walletRepository.findByUser(user);
        // Find currency that not in the wallets then add it to make the response more detail
        for (Symbol value : Symbol.values()) {
            boolean isInWallet = wallets.stream().anyMatch(x -> value.getBaseCurrency().equals(x.getCurrency()));
            if (!isInWallet) {
                Wallet wallet = new Wallet();
                wallet.setCurrency(value.getBaseCurrency());
                wallet.setUser(user);
                // Default 0
                wallet.setBalance(BigDecimal.ZERO);
                wallets.add(wallet);
            }
        }

        wallets.sort(Comparator.comparing(Wallet::getBalance, Comparator.nullsLast(Comparator.reverseOrder())));
        return wallets.stream().map(x -> modelMapper.map(x, UserWalletResponse.class)).collect(Collectors.toList());
    }
}