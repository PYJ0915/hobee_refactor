package hobee.semi.project.footer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CS {
	
	private int csNo;
	private String csWriterName;
	private String csWriterEmail;
	private String csContent;
	private String csWriteDate;
	private String csCompleteYn;
	
}
