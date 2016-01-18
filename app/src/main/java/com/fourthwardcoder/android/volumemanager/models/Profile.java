package com.fourthwardcoder.android.volumemanager.models;

import com.fourthwardcoder.android.volumemanager.interfaces.Constants;

import java.util.UUID;

public class Profile implements Constants {
	
	/*****************************************************/
	/*                    Constants                      */
	/*****************************************************/
	
	/*****************************************************/
	/*                   Local Data                      */
	/*****************************************************/
	private UUID id;
	private String title;
	private boolean enabled;
	private int startVolumeType, endVolumeType;
	private int startRingVolume, endRingVolume;
	private int previousVolumeType, previousRingVolume;

	public Profile() {
		
       id = UUID.randomUUID();
       enabled = true;
	   startVolumeType = VOLUME_OFF;
	   endVolumeType = VOLUME_VIBRATE;
	   startRingVolume = 1;
	   endRingVolume = 1;
	   previousVolumeType = VOLUME_VIBRATE;
	   previousRingVolume = 1;

	}

	@Override
	public String toString() {
		return title;
	}
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getStartVolumeType() {
		return startVolumeType;
	}

	public void setStartVolumeType(int startVolumeType) {
		this.startVolumeType = startVolumeType;
	}

	public int getEndVolumeType() {
		return endVolumeType;
	}

	public void setEndVolumeType(int endVolumeType) {
		this.endVolumeType = endVolumeType;
	}

	public int getStartRingVolume() {
		return startRingVolume;
	}

	public void setStartRingVolume(int startRingVolume) {
		this.startRingVolume = startRingVolume;
	}

	public int getEndRingVolume() {
		return endRingVolume;
	}

	public void setEndRingVolume(int endRingVolume) {
		this.endRingVolume = endRingVolume;
	}
	
	public int getPreviousVolumeType() {
		return previousVolumeType;
	}

	public void setPreviousVolumeType(int previousVolumeType) {
		this.previousVolumeType = previousVolumeType;
	}

	public int getPreviousRingVolume() {
		return previousRingVolume;
	}

	public void setPreviousRingVolume(int previousRingVolume) {
		this.previousRingVolume = previousRingVolume;
	}

}
