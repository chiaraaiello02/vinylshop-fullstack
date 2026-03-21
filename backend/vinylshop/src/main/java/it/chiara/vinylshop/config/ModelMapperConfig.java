package it.chiara.vinylshop.config;

import it.chiara.vinylshop.dtos.VinileDto;
import it.chiara.vinylshop.entities.Vinile;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // non sovrascrive i campi già presenti con null
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        // mapping custom entity -> dto
        modelMapper.addMappings(vinileMapping);

        // converter per stringhe (trim + null -> "")
        modelMapper.addConverter(stringTrimConverter);

        return modelMapper;
    }

    // Esempio: se nel  VinileDto ho un campo "dataCreaz" e voglio copiarlo dall'entity
    // (se non esiste nel dto, elimino questa PropertyMap)
    private final PropertyMap<Vinile, VinileDto> vinileMapping = new PropertyMap<>() {
        @Override
        protected void configure() {
            // Se hai dataCreaz nel DTO:
            // map().setDataCreaz(source.getDataCreaz());
        }
    };

    private final Converter<String, String> stringTrimConverter = new Converter<>() {
        @Override
        public String convert(MappingContext<String, String> context) {
            return context.getSource() == null ? "" : context.getSource().trim();
        }
    };
}
