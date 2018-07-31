package com.kustomer.kustomersdk.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.ViewHolders.AgentMessageViewHolder;
import com.kustomer.kustomersdk.ViewHolders.DummyViewHolder;
import com.kustomer.kustomersdk.ViewHolders.UserMessageViewHolder;

/**
 * Created by Junaid on 1/19/2018.
 */

public class MessageListAdapter extends RecyclerView.Adapter {

    //region Properties
    private static final int K_PREFETCH_PADDING = 20;
    private static final int K_5_MINUTE = 5 * 60 * 1000;

    private static final int AGENT_VIEW = 0;
    private static final int USER_VIEW = 1;
    private static final int END_VIEW = 2;

    private KUSPaginatedDataSource mPaginatedDataSource;
    private KUSUserSession mUserSession;
    private KUSChatMessagesDataSource mChatMessagesDataSource;
    private ChatMessageItemListener mListener;
    //endregion

    //region LifeCycle
    public MessageListAdapter(KUSPaginatedDataSource paginatedDataSource,
                              KUSUserSession userSession,
                              KUSChatMessagesDataSource chatMessagesDataSource,
                              ChatMessageItemListener listener) {
        mPaginatedDataSource = paginatedDataSource;
        mUserSession = userSession;
        mChatMessagesDataSource = chatMessagesDataSource;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == USER_VIEW)
            return new UserMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_view_holder, parent, false));
        else if (viewType == AGENT_VIEW)
            return new AgentMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agent_view_holder, parent, false));
        else
            return new DummyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_closed_chat_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == END_VIEW)
            return;

        KUSChatMessage chatMessage = messageForPosition(position);
        KUSChatMessage previousChatMessage = previousMessage(position);
        KUSChatMessage nextChatMessage = nextMessage(position);

        if (!mChatMessagesDataSource.isFetchedAll() &&
                position >= mChatMessagesDataSource.getSize() - 1 - K_PREFETCH_PADDING)
            mChatMessagesDataSource.fetchNext();

        boolean nextMessageOlderThan5Min = nextChatMessage == null ||
                nextChatMessage.getCreatedAt().getTime() - chatMessage.getCreatedAt().getTime() > K_5_MINUTE;

        if (holder.getItemViewType() == USER_VIEW) {
            ((UserMessageViewHolder) holder).onBind(chatMessage, nextMessageOlderThan5Min, mListener);
        } else if (holder.getItemViewType() == AGENT_VIEW) {
            boolean previousMessageDiffSender = !KUSChatMessage.KUSMessagesSameSender(previousChatMessage, chatMessage);
            ((AgentMessageViewHolder) holder).onBind(chatMessage, mUserSession,
                    previousMessageDiffSender, nextMessageOlderThan5Min, mListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        KUSChatMessage chatMessage = messageForPosition(position);

        boolean currentUser = KUSChatMessage.KUSChatMessageSentByUser(chatMessage);
        if (position >= mPaginatedDataSource.getSize())
            return END_VIEW;
        if (currentUser)
            return USER_VIEW;
        else
            return AGENT_VIEW;
    }

    @Override
    public int getItemCount() {
        if (mChatMessagesDataSource.isChatClosed())
            return mPaginatedDataSource.getSize() + 1;
        return mPaginatedDataSource.getSize();
    }
    //endregion

    //region Private Methods
    private KUSChatMessage messageForPosition(int position) {
        return (KUSChatMessage) mPaginatedDataSource.get(position);
    }

    private KUSChatMessage previousMessage(int position) {
        if (position < mChatMessagesDataSource.getSize() - 1 && position >= 0) {
            return messageForPosition(position + 1);
        } else {
            return null;
        }
    }

    private KUSChatMessage nextMessage(int position) {
        if (position > 0 && position < mChatMessagesDataSource.getSize()) {
            return messageForPosition(position - 1);
        } else {
            return null;
        }
    }
    //endregion

    //region Interface
    public interface ChatMessageItemListener {
        void onChatMessageImageClicked(KUSChatMessage chatMessage);

        void onChatMessageErrorClicked(KUSChatMessage chatMessage);
    }
    //endregion
}
