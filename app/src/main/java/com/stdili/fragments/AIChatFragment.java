package com.stdili.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.adapters.MessageAdapter;
import com.stdili.models.Message;
import com.stdili.services.LocalAIService;
import com.stdili.utils.ModerationUtils;
import java.util.ArrayList;
import java.util.List;

public class AIChatFragment extends Fragment {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private List<Message> messages;
    private MessageAdapter adapter;

    private final LocalAIService localAIService = new LocalAIService();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_chat, container, false);

        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMessages.setAdapter(adapter);

        // Add welcome message
        messages.add(new Message("Hi! I'm your AI study assistant. How can I help you with your studies today?", false));
        adapter.notifyDataSetChanged();

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            if (ModerationUtils.containsBadWords(text)) {
                // Show warning
                return;
            }

            messages.add(new Message(text, true));
            adapter.notifyDataSetChanged();
            etMessage.setText("");

            generateLocalResponse(text);
        }
    }

    private void generateLocalResponse(String prompt) {
        // simple typing indicator
        messages.add(new Message("...", false));
        adapter.notifyDataSetChanged();
        rvMessages.smoothScrollToPosition(messages.size() - 1);

        localAIService.chatReply(prompt, new LocalAIService.OnResponse() {
            @Override
            public void onSuccess(String response) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    removeTypingIfPresent();
                    messages.add(new Message(response, false));
                    adapter.notifyDataSetChanged();
                    rvMessages.smoothScrollToPosition(messages.size() - 1);
                });
            }

            @Override
            public void onFailure(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    removeTypingIfPresent();
                    messages.add(new Message("I couldn't generate a reply. Please try again.", false));
                    adapter.notifyDataSetChanged();
                    rvMessages.smoothScrollToPosition(messages.size() - 1);
                });
            }
        });
    }

    private void removeTypingIfPresent() {
        if (!messages.isEmpty() && "...".equals(messages.get(messages.size() - 1).getText())) {
            messages.remove(messages.size() - 1);
        }
    }
}