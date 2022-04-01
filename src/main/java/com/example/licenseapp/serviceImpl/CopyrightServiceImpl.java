package com.example.licenseapp.serviceImpl;

import com.example.licenseapp.dto.CopyrightDTO;
import com.example.licenseapp.mapper.CopyrightMapper;
import com.example.licenseapp.model.Copyright;
import com.example.licenseapp.repository.CopyrightRepository;
import com.example.licenseapp.service.CopyrightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Сервис взаимодействия с авторскими правами.
 *
 * Реализация через JpaRepository
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CopyrightServiceImpl implements CopyrightService {

    private final CopyrightMapper copyrightMapper;

    private final CopyrightRepository copyrightRepository;

    @Override
    public Page<Copyright> getAll(Pageable pageable) {
        log.debug("Получение всех авторских прав");

        return copyrightRepository.findAll(pageable);
    }

    @Override
    public CopyrightDTO getById(long id) {
        log.debug("Получение авторского права с id = {}", id);

        Copyright copyright = Optional.of(copyrightRepository.getById(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Компания не найдена"));

        return copyrightMapper.toDTO(copyright);
    }

    @Override
    @Transactional
    public CopyrightDTO create(CopyrightDTO copyrightDTO) {
        log.debug("Создание авторского права = {}", copyrightDTO);

        Copyright copyright = copyrightMapper.toEntity(copyrightDTO);
        Copyright savedCopyright = copyrightRepository.save(copyright);

        return copyrightMapper.toDTO(savedCopyright);
    }

    @Override
    @Transactional
    public CopyrightDTO update(CopyrightDTO copyrightDTO) {
        log.debug("Обновление авторского права с id = {}", copyrightDTO.id());

        Copyright copyright = copyrightMapper.toEntity(copyrightDTO);
        Copyright savedCopyright = copyrightRepository.save(copyright);

        return copyrightMapper.toDTO(savedCopyright);
    }

    @Override
    @Transactional
    public void delete(long id) {
        log.debug("Удаление авторского права с id = {}", id);

        copyrightRepository.deleteById(id);
    }

    @Override
    public List<CopyrightDTO> copyrightsByDates(LocalDate from, LocalDate to) {
        log.debug("Получение авторских прав в рамках дат с {} по {}", from, to);

        List<Copyright> copyrightsByExpireDtBetween = copyrightRepository.getCopyrightsByExpireDtBetween(from, to);
        return copyrightMapper.ToDtoList(copyrightsByExpireDtBetween);
    }

    @Override
    public List<CopyrightDTO> copyrightByCompanyName(String companyName) {
        log.debug("Получение авторских прав по названию компании = {}", companyName);

        List<Copyright> copyrightsByCompanyName = copyrightRepository.getCopyrightsByCompanyName(companyName);
//        if (CollectionUtils.isEmpty(copyrightsByCompanyName)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Авторские права компании " + companyName + " не найдены");
//        }

        return copyrightMapper.ToDtoList(copyrightsByCompanyName);
    }
}
