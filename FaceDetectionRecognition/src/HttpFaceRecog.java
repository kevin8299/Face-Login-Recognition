/**
 * Handles Face++ API to do face recognition
 * 
 * @author kevin
 * 
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

public class HttpFaceRecog {
	HttpRequests httpRequests = new HttpRequests(
			"4480afa9b8b364e30ba03819f3e9eff5",
			"Pz9VFT8AP3g_Pz8_dz84cRY_bz8_Pz8M", true, true);
	JSONObject result = null;

	String path;
	int groupN = 1;

	/**
	 * Constructor to the class 
	 */
	HttpFaceRecog(String p) {
		path = p;
	}

	/**
	 * Init to clear group info for the first time
	 */
	public void clear() {
		try {
			int existGroupNum = httpRequests.infoGetGroupList()
					.getJSONArray("group").length();
			for (int i = 0; i < existGroupNum; i++) {
				String gName = httpRequests.infoGetGroupList()
						.getJSONArray("group").getJSONObject(i)
						.getString("group_name");
				System.out.println(httpRequests
						.groupDelete(new PostParameters().setGroupName(gName)));
			}
			int existpersonNum = httpRequests.infoGetPersonList()
					.getJSONArray("person").length();
			for (int i = 0; i < existpersonNum; i++) {
				String pName = httpRequests.infoGetGroupList()
						.getJSONArray("person").getJSONObject(i)
						.getString("person_name");
				System.out
						.println(httpRequests.groupDelete(new PostParameters()
								.setPersonName(pName)));
			}
		} catch (FaceppParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("HttpFaceRecog :clear() finally");
		}
	}

	/**
	 * Upload sample images via Face++ API
	 * 
	 * @param allSam: id + image
	 */
	public void uploadSamples(Map<String, ArrayList<String>> allSam) {
		try {
			for (int k = 0; k < groupN; k++) {
				String groupName = "group_" + k;
				System.out.println(httpRequests
						.groupCreate(new PostParameters()
								.setGroupName(groupName)));
				ArrayList<String> personList = new ArrayList<String>();
				JSONObject syncRet = null;
				Iterator<String> iter = allSam.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					String personName = "person_" + key;
					System.out.println(httpRequests
							.personCreate(new PostParameters()
									.setPersonName(personName)));
					ArrayList<String> ids = allSam.get(key);
					for (int j = 0; j < ids.size(); j++) {
						String f = path + key + "_" + ids.get(j) + ".jpg";
						result = httpRequests
								.detectionDetect(new PostParameters()
										.setImg(new File(f)));
						System.out.println(result);
						System.out
								.println(httpRequests
										.personAddFace(new PostParameters()
												.setPersonName(personName)
												.setFaceId(
														result.getJSONArray(
																"face")
																.getJSONObject(
																		0)
																.getString(
																		"face_id"))));
					}
					personList.add(personName);
				}
				System.out.println(httpRequests
						.groupAddPerson(new PostParameters().setGroupName(
								"group" + k).setPersonName(personList)));
				System.out.println("\ntrain/Identify");
				syncRet = httpRequests.trainIdentify(new PostParameters()
						.setGroupName(groupName));
				System.out.println(syncRet);
				System.out.println(httpRequests.getSessionSync(syncRet
						.getString("session_id")));
			}
		} catch (FaceppParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("HttpFaceRecog :uploadSamples() finally");
		}
	}

	/**
	 * Recognize image via Face++ API
	 * 
	 * @param file: image to identify
	 * @return: recognized id
	 */
	public String recognize(String file) {
		String out = "";
		try {
			int existGroupNum = httpRequests.infoGetGroupList()
					.getJSONArray("group").length();
			for (int k = 0; k < existGroupNum; k++) {
				String groupName = "group_" + k;
				String ff = file;
				JSONObject recog = httpRequests
						.recognitionCompare(new PostParameters().setGroupName(
								groupName).setImg(new File(ff)));
				int num = recog.getJSONArray("face").getJSONObject(0)
						.getJSONArray("candidate").length();
				if (num > 1) {
					double winnerConf = recog.getJSONArray("face")
							.getJSONObject(0).getJSONArray("candidate")
							.getJSONObject(0).getDouble("confidence");
					String winnerName = recog.getJSONArray("face")
							.getJSONObject(0).getJSONArray("candidate")
							.getJSONObject(0).getString("person_name");
					for (int a = 1; a < num; a++) {
						double confidence = recog.getJSONArray("face")
								.getJSONObject(0).getJSONArray("candidate")
								.getJSONObject(0).getDouble("confidence");
						String personName = recog.getJSONArray("face")
								.getJSONObject(0).getJSONArray("candidate")
								.getJSONObject(0).getString("person_name");
						// system.out.println("Result:"+personName+" "+confidence);
						if (confidence > winnerConf) {
							winnerConf = confidence;
							winnerName = personName;
						}
					}
					System.out
							.println("Result:" + winnerName + "" + winnerConf);
					out = winnerName + "_" + winnerConf;
				}

			}
		} catch (FaceppParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("HttpFaceRecog :recongnize() finally");
		}
		return out;
	}
}
