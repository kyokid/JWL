package jwl.fpt.service.imp.BookBorrowService;

import jwl.fpt.model.BorrowCart;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BorrowerDto;
import jwl.fpt.model.dto.RfidDto;
import jwl.fpt.model.dto.RfidDtoList;
import jwl.fpt.repository.AccountRepository;

/**
 * Created by Entaard on 2/27/17.
 */
class BookBorrowServiceValidator {
    static boolean validateBorrowerDtoForInit(BorrowerDto borrowerDto,
                                              AccountRepository accountRepository,
                                              boolean isLibrarian) {
        if (!checkBorrowerDto(borrowerDto)) {
            return false;
        }

        String checkedUserId;
        if (!isLibrarian) {
            checkedUserId = accountRepository.checkBorrower(borrowerDto.getUserId());
        } else {
            checkedUserId = accountRepository.checkBorrowerByLibrarian(borrowerDto.getUserId());
        }

        return !(checkedUserId == null);
    }

    static boolean validateBorrowerDtoForCheckout(BorrowerDto borrowerDto) {
        return checkBorrowerDto(borrowerDto);
    }

    static boolean validateRfidDtoList(RfidDtoList rfidDtoList) {
        if (rfidDtoList == null
                || rfidDtoList.getIbeaconId() == null || rfidDtoList.getRfids() == null
                || rfidDtoList.getIbeaconId().isEmpty() || rfidDtoList.getRfids().isEmpty()) {
            return false;
        }

        return true;
    }

    static boolean validateRfidDto(RfidDto rfidDto) {
        if (rfidDto == null
                || rfidDto.getIbeaconId() == null || rfidDto.getRfid() == null
                || rfidDto.getIbeaconId().isEmpty() || rfidDto.getRfid().isEmpty()) {
            return false;
        }
        return true;
    }

    static RestServiceModel validateFoundBorrowCart(BorrowCart borrowCart) {
        RestServiceModel result = new RestServiceModel();
        if (borrowCart == null) {
            result.setFailData(
                    null,
                    "Borrow cart not found! Please contact librarian!",
                    "Error! Please contact librarian!");
            return result;
        }

        String userId = borrowCart.getUserId();
        if (userId == null || userId.isEmpty()) {
            result.setFailData(
                    null,
                    "UserId not found! Please contact librarian!",
                    "Error! Please contact librarian!");
            return result;
        }

        return null;
    }

    static private boolean checkBorrowerDto(BorrowerDto borrowerDto) {
        if (borrowerDto == null
                || borrowerDto.getIBeaconId() == null || borrowerDto.getUserId() == null
                || borrowerDto.getIBeaconId().isEmpty() || borrowerDto.getUserId().isEmpty()) {
            return false;
        }
        return true;
    }
}
