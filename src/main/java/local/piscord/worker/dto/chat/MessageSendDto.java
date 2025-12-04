package local.piscord.worker.dto.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RegisterForReflection
public record MessageSendDto(
                @NotBlank(message = "Room ID is required") String roomId,

                @NotNull(message = "Author data is required") UserSummaryDto author,

                String content,

                String fileUrl,

                MessagePreviewDto replyTo,

                @NotNull String sentAt) {

        @RegisterForReflection
        public record UserSummaryDto(
                        @NotBlank String id,

                        @NotBlank String username,

                        String picture) {
        }

        @RegisterForReflection
        public record MessagePreviewDto(
                        @NotBlank String id,

                        @NotBlank String content,

                        @NotNull UserSummaryDto author,

                        @NotNull String sentAt) {
        }
}