package bma.siteone.comments.service;

import org.apache.thrift.TException;

import bma.common.langutil.bean.copy.BeanCopyTool;
import bma.common.langutil.convert.common.DateFormatConverter;
import bma.common.langutil.core.Function;
import bma.common.langutil.core.ListUtil;
import bma.common.langutil.core.PagerResult;
import bma.siteone.comments.po.CommentInfo;
import bma.siteone.comments.po.CommentPoint;
import bma.siteone.comments.thrift.TCommentForm;
import bma.siteone.comments.thrift.TCommentHome;
import bma.siteone.comments.thrift.TCommentInfo;
import bma.siteone.comments.thrift.TCommentPoint;
import bma.siteone.comments.thrift.TCommentPointForm;
import bma.siteone.comments.thrift.TCommentPointSearchResult;
import bma.siteone.comments.thrift.TCommentSearchResult;
import bma.siteone.comments.thrift.TCommentsService;
import bma.siteone.comments.thrift.TSearchCommentForm;
import bma.siteone.comments.thrift.TSearchCommentPointForm;

public class CommentsServiceThrift implements TCommentsService.Iface {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CommentsServiceThrift.class);

	private CommentsService service;

	private transient BeanCopyTool source;
	private transient BeanCopyTool target;

	public CommentsServiceThrift() {
		super();
		source = new BeanCopyTool();
		initBeanCopy(source);
		target = new BeanCopyTool();
		target.setSourceStruct(false);
		initBeanCopy(target);
	}

	protected void initBeanCopy(BeanCopyTool tool) {
		tool.field("createTime").converter(DateFormatConverter.DATE_TIME);
		tool.field("lastUpdateTime").converter(DateFormatConverter.DATE_TIME);
		tool.field("lastCommentTime").converter(DateFormatConverter.DATE_TIME);
	}

	public CommentsService getService() {
		return service;
	}

	public void setService(CommentsService service) {
		this.service = service;
	}

	@Override
	public int createComment(TCommentForm info, TCommentPointForm pointForm)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("createComment({},{})", info, pointForm);
		}
		CommentForm infoObj = target.newInstance(null, info, CommentForm.class);
		CommentPointForm pfObj = target.newInstance(null, pointForm,
				CommentPointForm.class);
		return service.createComment(infoObj, pfObj);
	}

	@Override
	public boolean deleteComment(int id) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteComment({},{})", id);
		}
		return service.deleteComment(id);
	}

	@Override
	public TCommentInfo getComment(int id) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getComment({})", id);
		}
		CommentInfo info = service.getComment(id);
		if (info == null)
			return new TCommentInfo();
		return source.newInstance(null, info, TCommentInfo.class);
	}

	class CommentCast implements Function<CommentInfo, TCommentInfo> {
		@Override
		public TCommentInfo apply(CommentInfo input) {
			return source.newInstance(null, input, TCommentInfo.class);
		}
	}

	@Override
	public TCommentSearchResult searchComment(TSearchCommentForm form)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("searchComment({})", form);
		}
		SearchCommentForm fObj = target.newInstance(null, form,
				SearchCommentForm.class);
		PagerResult<CommentInfo> pr = service.searchComment(fObj);

		TCommentSearchResult r = new TCommentSearchResult();
		r.setTotal(pr.getPager().getTotal());
		r.setResult(ListUtil.toList(pr.getResult(), new CommentCast()));
		return r;
	}

	@Override
	public TCommentSearchResult listComment(int pointId, int page, int pageSize)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listComment({},{},{})", new Object[] { pointId, page,
					pageSize });
		}
		PagerResult<CommentInfo> pr = service.listComment(pointId, page,
				pageSize);

		TCommentSearchResult r = new TCommentSearchResult();
		r.setTotal(pr.getPager().getTotal());
		r.setResult(ListUtil.toList(pr.getResult(), new CommentCast()));
		return r;
	}

	@Override
	public boolean supportComment(int id, boolean oppose) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("supportComment({},{})", id, oppose);
		}
		return service.supportComment(id, oppose);
	}

	@Override
	public boolean authComment(int id, boolean authDone) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("authComment({},{})", id, authDone);
		}
		return service.authComment(id, authDone);
	}

	@Override
	public boolean reportComment(int id, boolean hide) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("reportComment({},{})", id, hide);
		}
		return service.reportComment(id, hide);
	}

	@Override
	public int createCommentPoint(TCommentPointForm pointForm)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("createCommentPoint({},{})", pointForm);
		}
		CommentPointForm fObj = target.newInstance(null, pointForm,
				CommentPointForm.class);
		return service.createCommentPoint(fObj);
	}

	@Override
	public boolean updateCommentPoint(int id, TCommentPointForm pointForm)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("updateCommentPoint({},{})", id, pointForm);
		}
		CommentPointForm fObj = target.newInstance(null, pointForm,
				CommentPointForm.class);
		return service.updateCommentPoint(id, fObj);
	}

	@Override
	public boolean deleteCommentPoint(int id) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteCommentPoint({})", id);
		}
		return service.deleteCommentPoint(id);
	}

	@Override
	public TCommentPoint getCommentPoint(int id) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getCommentPoint({})", id);
		}
		CommentPoint cp = service.getCommentPoint(id);
		if (cp == null)
			return new TCommentPoint();
		return source.newInstance(null, cp, TCommentPoint.class);
	}

	@Override
	public TCommentPoint getCommentPointByName(String name) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getCommentPointByName({})", name);
		}
		CommentPoint cp = service.getCommentPoint(name);
		if (cp == null)
			return new TCommentPoint();
		return source.newInstance(null, cp, TCommentPoint.class);
	}

	@Override
	public TCommentPointSearchResult searchCommentPoint(
			TSearchCommentPointForm form) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("searchCommentPoint({})", form);
		}
		SearchCommentPointForm fObj = target.newInstance(null, form,
				SearchCommentPointForm.class);
		PagerResult<CommentPoint> pr = service.searchCommentPoint(fObj);

		TCommentPointSearchResult r = new TCommentPointSearchResult();
		r.setTotal(pr.getPager().getTotal());
		r.setResult(ListUtil.toList(pr.getResult(),
				new Function<CommentPoint, TCommentPoint>() {
					@Override
					public TCommentPoint apply(CommentPoint input) {
						return source.newInstance(null, input,
								TCommentPoint.class);
					}
				}));
		return r;
	}

	@Override
	public TCommentHome getHome(String point, int pageSize) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getHome({},{})", point, pageSize);
		}
		CommentHome home = service.getHome(point, pageSize);
		TCommentHome r = new TCommentHome();
		if (home == null) {
			return r;
		}
		if (!home.isValid()) {
			return r;
		}
		CommentPoint cp = home.sure();
		r.setPoint(source.newInstance(null, cp, TCommentPoint.class));
		r.setTotal(home.getTotal());
		r.setResult(ListUtil.toList(home.getComments(),
				new Function<Integer, TCommentInfo>() {
					@Override
					public TCommentInfo apply(Integer input) {
						CommentInfo info = service.getComment(input);
						if (info != null) {
							return source.newInstance(null, info,
									TCommentInfo.class);
						}
						return null;
					}
				}));
		return r;
	}

}
