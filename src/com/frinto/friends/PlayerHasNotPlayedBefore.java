package com.frinto.friends;

@SuppressWarnings("serial")
public class PlayerHasNotPlayedBefore extends Exception
{
    public PlayerHasNotPlayedBefore()
    {

    }

    /**
     * @param message error message specific to cause of error.
     */
    public PlayerHasNotPlayedBefore(String message)
    {
        super(message);
    }
}
