package com.jeognykun.triple.mileage.mileageservice.service;

import com.jeognykun.triple.mileage.mileageservice.domain.Event;
import com.jeognykun.triple.mileage.mileageservice.domain.Place;
import com.jeognykun.triple.mileage.mileageservice.domain.PlaceHistory;
import com.jeognykun.triple.mileage.mileageservice.dto.EventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
@Primary
public class EventAddService implements IEventActionService {

    private final EventService eventService;
    private final PlaceService placeService;
    private final CalculatePointService calculatePointService;
    private final ReviewSaveService reviewSaveService;
    private final PlaceSaveService placeSaveService;

    @Override
    public Event eventAction(EventRequest req) {

        //event content & photoId exist check
        eventService.checkContentAndPhotoId(req);

        Place place = placeService.getPlace(req.getPlaceId());

        long contentAndPhotoPoint = calculatePointService.contentAndPhotoPointCalculate(req);
        long placePoint = placeService.getPlaceMileage(req,place);
        long mileage = contentAndPhotoPoint + placePoint;

        PlaceHistory placeHistory = placeService.isPlace(req.getPlaceId(), req.getReviewId(), req.getUserId());
        Event event = eventService.writeEvent(req, mileage);
        reviewSaveService.save(req);
        placeSaveService.save(req);
        placeService.savePlaceHistory(placeHistory);

        return event;
    }
}
