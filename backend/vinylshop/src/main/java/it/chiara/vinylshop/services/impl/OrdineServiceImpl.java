package it.chiara.vinylshop.services.impl;

import it.chiara.vinylshop.dtos.OrdineDto;
import it.chiara.vinylshop.dtos.OrdineItemDto;
import it.chiara.vinylshop.dtos.VinileSummaryDto;
import it.chiara.vinylshop.entities.*;
import it.chiara.vinylshop.repository.*;
import it.chiara.vinylshop.services.api.OrdineService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdineServiceImpl implements OrdineService {

    private final ModelMapper modelMapper;
    private final OrdineRepository ordineRepository;
    private final OrdineItemRepository ordineItemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final VinileRepository vinileRepository;

    public OrdineServiceImpl(ModelMapper modelMapper,
                             OrdineRepository ordineRepository,
                             OrdineItemRepository ordineItemRepository,
                             UserRepository userRepository,
                             CartRepository cartRepository,
                             CartItemRepository cartItemRepository, VinileRepository vinileRepository) {
        this.modelMapper = modelMapper;
        this.ordineRepository = ordineRepository;
        this.ordineItemRepository = ordineItemRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.vinileRepository=vinileRepository;
    }

    // -------------------- Helpers Security --------------------

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private User currentUserEntity() {
        return userRepository.findByUsernameIgnoreCase(currentUsername())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }

    private void checkOwnershipOrAdmin(Ordine ordine) {
        if (isAdmin()) return;
        if (ordine.getUser() == null) throw new RuntimeException("Ordine non valido (user mancante)");

        String me = currentUsername();
        String owner = ordine.getUser().getUsername();

        if (owner == null || !owner.equalsIgnoreCase(me)) {
            throw new RuntimeException("Accesso non autorizzato");
        }
    }

    // -------------------- DTO mapping --------------------

    private OrdineDto toDto(Ordine ordine) {
        OrdineDto dto = new OrdineDto();
        dto.setId(ordine.getId());
        dto.setTotaleSpeso(ordine.getTotaleSpeso());
        dto.setDataOrdine(ordine.getDataOrdine());

        if (ordine.getUser() != null) {dto.setUserId(ordine.getUser().getId());
                                      dto.setUsername(ordine.getUser().getUsername());
        }
        List<OrdineItem> items = ordineItemRepository.findByOrdine_Id(ordine.getId());
        List<OrdineItemDto> itemDtos = items.stream()
                .map(this::toDto)
                .toList();

        dto.setItems(itemDtos);




        return dto;
    }

    private OrdineItemDto toDto(OrdineItem item) {
        OrdineItemDto dto = new OrdineItemDto();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setPrezzoUnitario(item.getPrezzoUnitario());
        dto.setSubtotale(item.getSubtotale());
        if (item.getVinile() != null) {
            dto.setVinile(modelMapper.map(item.getVinile(), it.chiara.vinylshop.dtos.VinileSummaryDto.class));
        }
        return dto;
    }

    // conversione in summarydto
    private VinileSummaryDto toSummaryDto(Vinile vinile) {
        VinileSummaryDto dto = new VinileSummaryDto();
        dto.setId(vinile.getId());
        dto.setCodVinile(vinile.getCodVinile());
        dto.setTitolo(vinile.getTitolo());
        dto.setArtista(vinile.getArtista());
        return dto;
    }

    // -------------------- Read --------------------

    @Override
    public List<OrdineDto> getAllOrdini() {

        List<Ordine> ordini = ordineRepository.findAllByOrderByDataOrdineDesc();
        System.out.println("ORDINI TROVATI:" +ordini.size());
        return ordini.stream().map(ordine -> {

            // prendo gli items dell’ordine
            List<OrdineItem> items = ordineItemRepository.findByOrdine_Id(ordine.getId());

            // li converto in DTO item (con dati dell'articolo del singolo ordine)
            List<OrdineItemDto> itemDtos = items.stream().map(item -> {
                System.out.println("ITEM ID = " + item.getId());
                System.out.println("SUBTOTALE LETTO ENTITY = " + item.getSubtotale());
                System.out.println("PREZZO LETTO ENTITY = " + item.getPrezzoUnitario());

                OrdineItemDto dto = new OrdineItemDto();
                dto.setId(item.getId());
                dto.setVinile(toSummaryDto(item.getVinile()));
                dto.setQuantity(item.getQuantity());
                dto.setPrezzoUnitario(item.getPrezzoUnitario());
                dto.setSubtotale(item.getSubtotale());

                return dto;
            }).toList();

            // creo ordine DTO completo
            OrdineDto dto = new OrdineDto();
            dto.setId(ordine.getId());
            dto.setDataOrdine(ordine.getDataOrdine());
            dto.setTotaleSpeso(ordine.getTotaleSpeso());
            dto.setItems(itemDtos);
            //salvo username e id dell'utente che ha comprato
            if (ordine.getUser() != null) {
                dto.setUserId(ordine.getUser().getId());
                dto.setUsername(ordine.getUser().getUsername());
            }

            return dto;

        }).toList();
    }

    @Override
    public List<OrdineDto> getOrdiniByUser(Long userId) {
        return ordineRepository.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }
    @Override
    public List<OrdineDto> getMyOrdini() {
        User me = currentUserEntity();

        List<Ordine> ordini = ordineRepository.findByUserId(me.getId());

        return ordini.stream().map(ordine -> {

            List<OrdineItem> items = ordineItemRepository.findByOrdine_Id(ordine.getId());

            List<OrdineItemDto> itemDtos = items.stream().map(item -> {
                OrdineItemDto dto = new OrdineItemDto();
                dto.setId(item.getId());
                dto.setVinile(toSummaryDto(item.getVinile()));
                dto.setQuantity(item.getQuantity());
                dto.setPrezzoUnitario(item.getPrezzoUnitario());
                dto.setSubtotale(item.getSubtotale());
                return dto;
            }).toList();

            OrdineDto dto = new OrdineDto();
            dto.setId(ordine.getId());
            dto.setDataOrdine(ordine.getDataOrdine());
            dto.setTotaleSpeso(ordine.getTotaleSpeso());
            dto.setItems(itemDtos);

            if (ordine.getUser() != null) {
                dto.setUserId(ordine.getUser().getId());
                dto.setUsername(ordine.getUser().getUsername());
            }

            return dto;
        }).toList();
    }

    public Page<OrdineDto> getOrdiniPaginati(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return ordineRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    public List<OrdineItemDto> getOrdineItemByOrdine(Long ordineId) {
        return ordineItemRepository.findByOrdine_Id(ordineId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<OrdineItemDto> getOrdineItemByOrdineProtected(Long ordineId) {
        Ordine ordine = ordineRepository.findById(ordineId)
                .orElseThrow(() -> new RuntimeException("Ordine non trovato"));
        checkOwnershipOrAdmin(ordine);
        return getOrdineItemByOrdine(ordineId);
    }

    // -------------------- Checkout --------------------
    @Override
    @Transactional
    public OrdineDto createOrderFromCart() {
        User user = currentUserEntity();
        System.out.println("CHECKOUT USER ID: " + user.getId());

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Carrello non trovato"));

        System.out.println("CART ID: " + cart.getId());

        List<CartItem> cartItems = cartItemRepository.findByCart_Id(cart.getId());
        System.out.println("NUMERO CART ITEMS: " + (cartItems == null ? "null" : cartItems.size()));

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Carrello vuoto");
        }

        Ordine ordine = new Ordine();
        ordine.setUser(user);
        ordine.setDataOrdine(LocalDateTime.now());
        ordine.setTotaleSpeso(0.0);

        ordine = ordineRepository.save(ordine);

        double totale = 0.0;

        for (CartItem ci : cartItems) {
            System.out.println("------ CART ITEM ------");
            System.out.println("CartItem ID: " + ci.getId());
            System.out.println("Quantity: " + ci.getQuantity());

            Vinile vinile = ci.getVinile();
            System.out.println("Vinile: " + (vinile == null ? "null" : vinile.getTitolo()));
            System.out.println("Stock vinile: " + (vinile == null ? "null" : vinile.getStock()));

            if (vinile == null) {
                throw new RuntimeException("Vinile non trovato nel carrello");
            }

            int qta = ci.getQuantity();

            if (qta <= 0) {
                throw new RuntimeException("Quantità non valida nel carrello");
            }

            if (qta > vinile.getStock()) {
                throw new RuntimeException("Stock insufficiente per il vinile: " + vinile.getTitolo());
            }

            double prezzoSnapshot = vinile.getPrezzo();

            OrdineItem oi = new OrdineItem();
            oi.setOrdine(ordine);
            oi.setVinile(vinile);
            oi.setQuantity(qta);
            oi.setPrezzoUnitario(prezzoSnapshot);
            oi.setSubtotale(prezzoSnapshot * qta);

            totale += oi.getSubtotale();
            ordineItemRepository.save(oi);

            vinile.setStock(vinile.getStock() - qta);
            vinileRepository.save(vinile);
        }

        ordine.setTotaleSpeso(totale);
        ordine = ordineRepository.save(ordine);

        cartItemRepository.deleteByCartId(cart.getId());

        System.out.println("ORDINE CREATO CON SUCCESSO. ID ORDINE: " + ordine.getId());
        System.out.println("TOTALE ORDINE: " + totale);

        return toDto(ordine);
    }

    // -------------------- Complete --------------------






}