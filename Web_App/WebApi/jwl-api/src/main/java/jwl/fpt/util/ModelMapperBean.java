package jwl.fpt.util;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 * Bean to @Autowired ModelMapper.
 */
@Service
public class ModelMapperBean extends ModelMapper {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
