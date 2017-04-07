package jwl.fpt.service.imp.BookBorrowService;

import jwl.fpt.model.BorrowCart;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BorrowerDto;
import jwl.fpt.model.dto.RfidDto;
import jwl.fpt.model.dto.RfidDtoList;
import jwl.fpt.repository.AccountRepository;
import jwl.fpt.util.Constant.*;

/**
 * Created by Entaard on 2/27/17.
 */
class BookBorrowServiceValidator {
    static boolean validateBorrowerDto(BorrowerDto borrowerDto,
                                       AccountRepository accountRepository,
                                       boolean isLibrarian) {
        if (borrowerDto == null || borrowerDto.getIBeaconId().isEmpty() || borrowerDto.getUserId().isEmpty()) {
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

    static RestServiceModel validateFoundBorrowCart(BorrowCart borrowCart, boolean isLibrarian) {
        RestServiceModel result = new RestServiceModel();
        String soundError = isLibrarian ? "" : SoundMessages.ERROR;
        String textError = isLibrarian ? "" : "Please contact librarian!";
        if (borrowCart == null) {
            result.setFailData(
                    null,
                    "Borrow cart not found! " + textError,
                    soundError);
            return result;
        }

        String userId = borrowCart.getUserId();
        if (userId == null || userId.isEmpty()) {
            result.setFailData(
                    null,
                    "UserId not found! " + textError,
                    soundError);
            return result;
        }

        return null;
    }
}
