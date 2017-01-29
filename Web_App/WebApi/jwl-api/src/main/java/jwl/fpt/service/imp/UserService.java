package jwl.fpt.service.imp;

import jwl.fpt.entity.TblUserEntity;
import jwl.fpt.model.dto.UserDto;
import jwl.fpt.repository.UserRepository;
import jwl.fpt.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaVH on 1/9/17.
 */
@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<UserDto> getAllUser() {
        List<TblUserEntity> users = userRepository.findAll();
        List<UserDto> results = new ArrayList<>();

        for (TblUserEntity user:
                users) {
            UserDto dto = modelMapper.map(user, UserDto.class);
            results.add(dto);
        }

        return results;
    }

    @Override
    public UserDto findByUsernameAndPassword(String username, String password) {
        TblUserEntity entity = userRepository.findByUsernameAndPassword(username, password);

        if (entity == null) {
            return null;
        }

        UserDto dto = modelMapper.map(entity, UserDto.class);

        return dto;
    }

    @Override
    public List<UserDto> findByUsernameLike(String q) {
        List<TblUserEntity> entities = userRepository.findByUsernameLike('%' +q + '%');
        List<UserDto> results = new ArrayList<>();

        for (TblUserEntity entity :
                entities) {
            UserDto dto = modelMapper.map(entity, UserDto.class);
            results.add(dto);
        }

        return results;
    }
}
