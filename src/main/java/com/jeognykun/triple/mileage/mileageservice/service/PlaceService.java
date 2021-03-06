package com.jeognykun.triple.mileage.mileageservice.service;

import com.jeognykun.triple.mileage.mileageservice.domain.Place;
import com.jeognykun.triple.mileage.mileageservice.domain.PlaceHistory;
import com.jeognykun.triple.mileage.mileageservice.domain.PlaceId;
import com.jeognykun.triple.mileage.mileageservice.dto.EventRequest;
import com.jeognykun.triple.mileage.mileageservice.exception.PlaceWriteFailException;
import com.jeognykun.triple.mileage.mileageservice.repository.PlaceHistoryRepository;
import com.jeognykun.triple.mileage.mileageservice.repository.PlaceRepository;
import com.jeognykun.triple.mileage.mileageservice.type.ActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceHistoryRepository placeHistoryRepository;
    private final PlaceRepository placeRepository;

    public void savePlaceHistory(PlaceHistory placeHistory) {
        placeHistoryRepository.save(placeHistory);
    }

    public Place getPlace(String placeId) {
        return placeRepository.findById(placeId).orElse(null);
    }

    public long getPlaceMileage(EventRequest dto, Place place) {
        if(isAvailableFirst(dto, place)) {
            setSpecialValue(dto.getPlaceId(), dto.getReviewId());
        } else {
            return 0;
        }

        return dto.getAction().equals(ActionType.DELETE) ? -1 : 1;
    }

    public void setSpecialPlace(Place place, String reviewId) {
        if(reviewId.equals(place.getReviewId())) {
            placeRepository.save(Place.builder()
                    .placeId(place.getPlaceId())
                    .reviewId(null)
                    .build());
        }
    }

    public void setSpecialValue(String placeId, String reviewId) {
        placeRepository.save(Place.builder()
                .placeId(placeId)
                .reviewId(reviewId)
                .build());
    }

    public boolean isAvailableFirst(EventRequest dto, Place place) {
        if(place != null) {
            if(dto.getAction().equals(ActionType.DELETE)) {
                return dto.getReviewId().equals(place.getReviewId());
            } else {
                return place.getReviewId() == null;
            }
        } else {
            return false;
        }
    }

    public PlaceHistory isPlace(String placeId, String reviewId, String placeUser) {
        PlaceId placeIds = PlaceId.builder()
                .placeId(placeId)
                .userId(placeUser)
                .build();

        Optional<PlaceHistory> placeHistory = placeHistoryRepository.findById(placeIds);

        if(placeHistory.isPresent()) {
            throw new PlaceWriteFailException(placeId,placeUser);
        }

        return PlaceHistory.builder()
                .placeId(placeIds)
                .reviewId(reviewId)
                .build();
    }

    public void deletePlaceHistory(String placeId, String userId) {
        PlaceId placeIds = PlaceId.builder()
                .placeId(placeId)
                .userId(userId)
                .build();

        placeHistoryRepository.deleteById(placeIds);
    }
}
